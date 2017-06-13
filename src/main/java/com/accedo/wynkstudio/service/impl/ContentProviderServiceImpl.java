package com.accedo.wynkstudio.service.impl;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import com.accedo.wynkstudio.common.CPConstants;
import com.accedo.wynkstudio.dao.ProductDao;
import com.accedo.wynkstudio.dao.UserProfileDao;
import com.accedo.wynkstudio.exception.BusinessApplicationException;
import com.accedo.wynkstudio.helper.AppgridHelper;
import com.accedo.wynkstudio.helper.ContentProviderHelper;
import com.accedo.wynkstudio.helper.SubscriptionHelper;
import com.accedo.wynkstudio.helper.UserHelper;
import com.accedo.wynkstudio.mongodb.dao.MongoDBUserDerivedProfileDAO;
import com.accedo.wynkstudio.mongodb.entity.User;
import com.accedo.wynkstudio.service.ContentProviderRegisterService;
import com.accedo.wynkstudio.service.ContentProviderService;
import com.accedo.wynkstudio.util.ContentProviderUtil;
import com.accedo.wynkstudio.util.JsonTransformation;
import com.accedo.wynkstudio.util.Util;
import com.accedo.wynkstudio.vo.ProductVO;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

@Service
@Transactional
public class ContentProviderServiceImpl implements ContentProviderService {

	@Autowired
	private ContentProviderRegisterService ContentProviderRegisterService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ProductDao productDao;

	@Autowired
	private UserProfileDao userProfileDao;
	

	@Autowired
	private MongoDBUserDerivedProfileDAO mongoDBUserDerivedProfileDAO;

	private HttpHeaders headers;
	private JsonObject mpxFeedJson;
	final Logger log = LoggerFactory.getLogger(this.getClass());

	@PostConstruct
	public void init() {
		ContentProviderRegisterService.setContentProvider(
				messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_CONTENTPROVIDER, null, "", Locale.ENGLISH),
				this);
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		AppgridHelper.getAssetsFromAppgridAndroid(headers);
		AppgridHelper.getAssetsFromAppgridAndroidForNewVersion(headers);
	}

	/*** Public Methods ***/

	@Override
	public String getProgramsByCategoryWithType(String cpId, String categoryId, String programType, String order,
			String sortingKey, String totalPageSize, String pageNumber, String pageSize) {
		/*
		 * Please check programtype is video,movie etc.
		 */
		try {
			return getProgramsList(cpId, categoryId, programType, order, sortingKey, totalPageSize, pageNumber,
					pageSize);
		} catch (Exception e) {
			log.error("Error, line 85 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
	}

	@Override
	public String getProgramById(String cpId, String videoId) {
		try {
			return getProgramByGuid(videoId);
		} catch (Exception e) {
			log.error("Error, line 95 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
	}

	@Override
	public String getSeasonById(String cpToken, String seasonId, String totalPageSize, String pageSize, String pageNumber) {
		String response = "";
		String episodesUrl = "";
		try {
			mpxFeedJson = AppgridHelper.mpxFeedData;
			episodesUrl = mpxFeedJson.get("episodelistbyseasonid").asString();
			response = ContentProviderUtil.createPagination(totalPageSize, pageSize, pageNumber, messageSource
					.getMessage(CPConstants.WYNKSTUDIO_MPX_PAGINATION_DEFAULT_START, null, "", Locale.ENGLISH),
					messageSource.getMessage(CPConstants.WYNKSTUDIO_MPX_PAGINATION_DEFAULT_END, null, "",
							Locale.ENGLISH));
			String[] range = response != null ? !response.isEmpty() ? response.trim().split("_") : null : null;
			if (range != null && range.length > 0) {
				episodesUrl = episodesUrl.replace("{0}", seasonId);
				episodesUrl = episodesUrl.replace("{1}", Integer.parseInt(range[0]) > 10000 ? "910" : range[0]);
				episodesUrl = episodesUrl.replace("{2}", Integer.parseInt(range[1]) > 10000 ? "1000" : range[1]);
				episodesUrl = cpToken.toUpperCase().contains("SONY") ? episodesUrl + "&sort=tvSeasonEpisodeNumber|desc" : episodesUrl + "&sort=tvSeasonEpisodeNumber|asc";
				response = Util.executeApiGetCall(episodesUrl);
				response = convertJsonToProgramModel(response);
			}
			else {
				response = messageSource
						.getMessage(CPConstants.WYNKSTUDIO_MPX_LIMIT_OVERFLOW, null, "", Locale.ENGLISH);
			}
		} catch (HttpClientErrorException e) {
			log.error("Error, line 111 -", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (Exception e) {
			log.error("Error, line 114 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return response;
	}

	@Override
	public String getSeasonBySeriesId(String cpId, String showId, String totalPageSize, String pageSize,
			String pageNumber) {
		String response = "";
		mpxFeedJson = AppgridHelper.mpxFeedData;
		String seriesUrl = mpxFeedJson.get("programbyid").asString();
		seriesUrl = seriesUrl.replace("{0}", showId);

		try {
			response = ContentProviderUtil.createPagination(totalPageSize, pageSize, pageNumber, messageSource
					.getMessage(CPConstants.WYNKSTUDIO_MPX_PAGINATION_DEFAULT_START, null, "", Locale.ENGLISH),
					messageSource.getMessage(CPConstants.WYNKSTUDIO_MPX_PAGINATION_DEFAULT_END, null, "",
							Locale.ENGLISH));
			String[] range = response != null ? !response.isEmpty() ? response.trim().split("_") : null : null;
			if (range != null && range.length > 0) {
				response = Util.executeApiGetCall(seriesUrl);
				JsonObject seriesJsonObject = JsonObject.readFrom(response);
				JsonArray seriesArray = seriesJsonObject.get("entries").asArray();
				JsonObject seriesObj = seriesArray.get(0).asObject();
				String seriesUri = seriesObj.get("id").asString();
				String episodesUrl = mpxFeedJson.get("episodelistbyseriesid").asString();
				episodesUrl = episodesUrl.replace("{0}", seriesUri);
				episodesUrl = episodesUrl.replace("{1}", Integer.parseInt(range[0]) > 10000 ? "910" : range[0]);
				episodesUrl = episodesUrl.replace("{2}", Integer.parseInt(range[1]) > 10000 ? "1000" : range[1]);
				response = Util.executeApiGetCall(episodesUrl);
				response = ContentProviderHelper.createJsonResponse(response);
			} else {
				response = messageSource
						.getMessage(CPConstants.WYNKSTUDIO_MPX_LIMIT_OVERFLOW, null, "", Locale.ENGLISH);
			}
		} catch (HttpClientErrorException e) {
			log.error("Error, line 164 -", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (Exception e) {
			log.error("Error, line 167 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return response;
	}

	@Override
	public String getBanner(String cpId) {
		String response = "";
		String baseURL = getBannerBaseUrlByCpId(cpId);
		try {
			response = Util.executeApiGetCall(baseURL);
			mpxFeedJson = AppgridHelper.mpxFeedData;
			String creditsByProgramIdsUrl = mpxFeedJson.get("creditsbyprogramid").asString();
			response = ContentProviderUtil.getProgramWithAvailableCredits(response, creditsByProgramIdsUrl, headers);
			response = ContentProviderHelper.createJsonResponse(response);
		} catch (HttpClientErrorException e) {
			log.error("Error, line 184 -", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (Exception e) {
			log.error("Error, line 187 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return response;
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getRelatedProgramById(String cpId, String guid) {
		String response = "";
		String baseURL = getProgramsBaseUrlById();
		try {
			baseURL = baseURL.replace("{0}", guid).concat("&fields=tags,year,programType,languages");
			response = Util.executeApiGetCall(baseURL);
			if (response != null) {
				JsonObject responseJson = JsonObject.readFrom(response);
				JsonObject programObject = responseJson.get("entries").asArray().size() > 0 ? responseJson
						.get("entries").asArray().get(0).asObject() : null;
				if (programObject != null) {
					String language = (programObject.get("languages") != null
							&& !programObject.get("languages").isNull() && programObject.get("languages").asArray()
							.size() > 0) ? programObject.get("languages").asArray().get(0).asString() : "";
					String programType = programObject.get("programType").asString();
					int year = (programObject.get("year") != null && !programObject.get("year").isNull()) ? programObject
							.get("year").asInt() : 0;
					JsonArray tagsJsonArray = (programObject.get("tags") != null && !programObject.get("tags").isNull()) ? programObject
							.get("tags").asArray() : null;
					if (tagsJsonArray != null && tagsJsonArray.size() > 0) {
						String category = getCategoryOrGenre(tagsJsonArray);
						String byTags = programType + "," + cpId + "," + URLEncoder.encode(category);
						if (tagsJsonArray
								.toString()
								.toLowerCase()
								.contains(
										messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_YOUTUBE, null, "",
												Locale.ENGLISH).toLowerCase())
								|| tagsJsonArray
										.toString()
										.toLowerCase()
										.contains(
												messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_DAILYMOTION,
														null, "", Locale.ENGLISH).toLowerCase())) {
							baseURL = mpxFeedJson.get("relatedprogrambytags").asString().replace("{0}", byTags)
									+ "&sort=pubDate|desc";
						} else {
							baseURL = mpxFeedJson.get("relatedprogrambytags").asString().replace("{0}", byTags)
									+ "&byLanguages=" + language;
							if (year != 0) {
								baseURL += "&byYear=" + (year * 1 - 5) + "~" + (year * 1 + 5);
							}

							baseURL = language != null
									&& !language.isEmpty()
									&& (language.toLowerCase().contains("eng")
											|| language.toLowerCase().contains("kor") || language.toLowerCase()
											.contains("tgl")) ? baseURL + "&sort=:imdbRating|desc" : baseURL
									+ "&sort=year|desc";
							// + "&sort=:imdbRating|desc";
						}

						response = Util.executeApiGetCall(baseURL);
						JsonObject movieJsonObject = JsonObject.readFrom(response);
						JsonArray entries = movieJsonObject.get("entries").asArray();
						entries = getOtherRelatedPrograms(guid, entries);
						if (entries.size() == 0
								&& (!tagsJsonArray
										.toString()
										.toLowerCase()
										.contains(
												messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_YOUTUBE, null,
														"", Locale.ENGLISH).toLowerCase()) || !tagsJsonArray
										.toString()
										.toLowerCase()
										.contains(
												messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_DAILYMOTION,
														null, "", Locale.ENGLISH).toLowerCase()))) {
							baseURL = baseURL.replace("&byYear=" + (year * 1 - 5) + "~" + (year * 1 + 5), "&byYear="
									+ (year * 1 - 10) + "~" + (year * 1 + 10));
							response = Util.executeApiGetCall(baseURL);
							movieJsonObject = JsonObject.readFrom(response);
							entries = movieJsonObject.get("entries").asArray();
							entries = getOtherRelatedPrograms(guid, entries);
						}
						movieJsonObject.set("entries", entries);
						response = movieJsonObject.toString();
					}
					mpxFeedJson = AppgridHelper.mpxFeedData;
					String creditsByProgramIdsUrl = mpxFeedJson.get("creditsbyprogramid").asString();
					response = ContentProviderUtil.getProgramWithAvailableCredits(response, creditsByProgramIdsUrl,
							headers);
					response = convertJsonToProgramModel(response);
				}
			}
		} catch (HttpClientErrorException e) {
			log.error("Error, line 237 -", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (Exception e) {
			log.error("Error, line 240 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return response;
	}

	private JsonArray getOtherRelatedPrograms(String guid, JsonArray entries) {
		for (int j = 0; j < entries.size(); j++) {
			JsonObject jsonContent = entries.get(j).asObject();
			if (jsonContent.get("guid").asString().equalsIgnoreCase(guid)) {
				entries.remove(j);
				break;
			}
		}
		return entries;
	}

	/* Get Sorting Options */
	@Override
	public String getSortingOptions(String cpToken, String language) {
		try {
			JsonObject sortObject = (JsonObject) AppgridHelper.appGridMetadata.get("cp_sorting_options");
			String formCp = "";
			if (language != null && !language.isEmpty()) {
				formCp = cpToken.toLowerCase() + "_" + language + "_sorting";
			} else {
				formCp = cpToken.toLowerCase() + "_sorting";
			}
			String sortOptions = (sortObject.get(formCp) != null && !sortObject.get(formCp).isNull()) ? sortObject.get(
					formCp).asString() : "{}";
			return sortOptions;
		} catch (Exception e) {
			log.error("Error, line 261 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
	}

	/* Get subscription plans */
	@Override
	public String getSubscriptionPlans(String cpId, String platform, String uid, String token) {
		String response = "";
		try {
			cpId = (cpId != null) ? cpId : "";
			String feedsString = (platform != null && !platform.isEmpty()
					&& platform.equalsIgnoreCase(messageSource
							.getMessage(CPConstants.WYNKSTUDIO_SUBSCRIPTION_PLATFORM_IOS, null, "", Locale.ENGLISH))
									? AppgridHelper.appGridMetadata
                                                                                .get("subscription_feed_ios").asString()
									: AppgridHelper.appGridMetadata.get("subscription_feed").asString())
					+ "?byProductTags=" + cpId;
			response = Util.executeApiGetCall(feedsString);
			response = response.replace("pl2$bundleFlag", "bundleFlag").replace("pl2$bundleLimit", "bundleLimit")
					.replace("pl2$isSubscription", "isSubscription").replace("pl1$itunesId", "itunesId")
					.replace("pl1$itunesPrice", "itunesPrice").replace("pl1$freePack", "freePack")
					.replace("com.airtel.wynkpremiere.hooq_25", "com.airtel.wynkpremiere.hooq_250")
                    .replace("com.airtel.wynkpremiere.erosnow_6", "com.airtel.wynkpremiere.erosnow_60")
					.replace("com.airtel.wynkpremiere.hooq_2500", "com.airtel.wynkpremiere.hooq_250")
					.replace("com.airtel.wynkpremiere.erosnow_600", "com.airtel.wynkpremiere.erosnow_60");
			response = JsonTransformation.transformJson(response, "/jsonSpec/mpx/plans.json");
			JsonObject responseJson = JsonObject.readFrom(response);
			JsonArray responseArray = responseJson.get("entries").asArray();
			JsonArray longDescriptionJsonArray = null;
			for (JsonValue responseObject : responseArray) {
				for (JsonValue cpObject : responseObject.asObject().get("productTags").asArray()) {
					if (cpObject.isObject() && cpObject.asObject().get("scheme").asString().equalsIgnoreCase("provider"))
						responseObject.asObject().set("contentProvider",
								cpObject.asObject().get("title").asString().toUpperCase());
				}
				
				String[] longDescription = responseObject.asObject().get("longDescription").asString().split("~");
				longDescriptionJsonArray = new JsonArray();
				if (longDescription.length > 0) {
					for (int i = 0; i < longDescription.length; i++) {
						if(!longDescription[i].isEmpty() ){
						longDescriptionJsonArray.add(longDescription[i]);
						}
					}
				}
				
				String starIconProducts = AppgridHelper.appGridMetadata.get("star_icon_products").asString();
				if(starIconProducts.contains(responseObject.asObject().get("id").asString()))
				{
					responseObject.asObject().set("starIcon",true);
				}
				else
				{
					responseObject.asObject().set("starIcon",false);
				}
			
				responseObject.asObject().set("longDescription", longDescriptionJsonArray);
			}
			responseJson.set("entries", responseArray);

			if (!uid.isEmpty() && !token.isEmpty()) {
				response = getUserSpecificPlans(responseJson, uid, token, platform);
			} else {
				response = responseJson.toString();
			}
		} catch (HttpClientErrorException e) {
			log.error("Error, line 291 -", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (Exception e) {
			log.error("Error, line 294 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return response;
	}

	private String getUserSpecificPlans(JsonObject responseJson, String uid, String token, String platform) {
		JsonArray entriesArray = responseJson.get("entries").asArray();
		List<ProductVO> productsList = productDao.getProductsByUserId(uid);
		JsonArray finalArray = new JsonArray();
		JsonArray sonyArray = new JsonArray();
		JsonArray erosArray = new JsonArray();
                JsonArray airtelArray = new JsonArray();
		JsonArray singtelArray = new JsonArray();
		String cps[] = new String[3];
		HashMap<String, JsonArray> cpProductsMap = new HashMap<String, JsonArray>();

		for (JsonValue entry : entriesArray) {
			switch (entry.asObject().get("contentProvider").asString().toUpperCase()) {
			case "SONYLIV":
				sonyArray.add(entry);
				break;
			case "SINGTEL":
				singtelArray.add(entry);
				break;
			case "EROSNOW":
				erosArray.add(entry);
				break;
                        case "AIRTEL":
                                airtelArray.add(entry);
                                break;
			}
		}

		if (singtelArray.size() > 0) {
			if (!platform.equalsIgnoreCase("ios") && userProfileDao.getHooqTrialFlag(uid)) {
//                            removing hooq 14days trial pack based on id
                              for (int i =0; i < singtelArray.size(); i++) {
                                  if (singtelArray.get(i).asObject().get("id").asString().equalsIgnoreCase("12906")){
                                      singtelArray.remove(i);
                                      break;
                                  }
                              }
//                            this removes hooq monthly pack as well, so commenting
//				singtelArray.remove(0);
//				singtelArray.remove(0); 
			} else if (platform.equalsIgnoreCase("ios")) {
				if (userProfileDao.getHooqTrialFlagIos(uid)) {
					singtelArray.get(0).asObject().set("trialFlag", false);
				} else {
					singtelArray.get(0).asObject().set("trialFlag", true);
				}
			} else if (!platform.equalsIgnoreCase("ios")) {
				if (checkUserType(uid)) {
					singtelArray.remove(1);
				} else {
					singtelArray.remove(0);
				}
			}
		}

		cpProductsMap.put("EROSNOW", erosArray);
		cpProductsMap.put("SINGTEL", singtelArray);
		cpProductsMap.put("SONYLIV", sonyArray);
                cpProductsMap.put("AIRTEL", airtelArray);
		String cpList = "";

		// Find Live products and make a list of product Ids
		for (ProductVO product : productsList) {
			if (product.getLive() && product.getProductType().equalsIgnoreCase("prime")) {
				cpList = cpList + "_" + product.getCpId().toUpperCase();
			}
		}

		// Re-order plans based on cps already activated.
		cpList = reOrderCps(cpList);
		cps = cpList.split("_");

		for (int i = 0; i < cps.length; i++) {
			JsonArray prdArr = cpProductsMap.get(cps[i].toUpperCase());
			for (int j = 0; j < prdArr.size(); j++) {
				finalArray.add(prdArr.get(j));
			}
		}

		responseJson.set("entries", finalArray);

		return responseJson.toString();
	}

	private String reOrderCps(String cpList) {
		if (cpList.contains("EROSNOW")) {
			if (cpList.contains("SINGTEL")) {
				if (cpList.contains("SONYLIV")) {
					cpList = "EROSNOW_SINGTEL_SONYLIV_AIRTEL";
				} else {
					cpList = "SONYLIV_EROSNOW_SINGTEL_AIRTEL";
				}
			} else if (cpList.contains("SONYLIV")) {
				cpList = "SINGTEL_EROSNOW_SONYLIV_AIRTEL";
			} else {
				cpList = "SINGTEL_SONYLIV_EROSNOW_AIRTEL";
			}
		} else {
			if (cpList.contains("SONYLIV")) {
				if (cpList.contains("SINGTEL")) {
					cpList = "EROSNOW_SINGTEL_SONYLIV_AIRTEL";
				} else {
					cpList = "SONYLIV_EROSNOW_SINGTEL_AIRTEL";
				}
			} else if (cpList.contains("SINGTEL")) {
				cpList = "EROSNOW_SONYLIV_SINGTEL_AIRTEL";
			} else {
				cpList = "EROSNOW_SINGTEL_SONYLIV_AIRTEL";
			}
		}

		return cpList;
	}
	
	/*
	 * Method to check if the user is POSTPAID/PREPAID from MongoDB Profile
	 * Returns true when POSTPAID and false otherwise
	 */
	private Boolean checkUserType(String userId) {
		Boolean userFlag = false;

		User userProfile = null;

		String bsbUserProfile = SubscriptionHelper.getUserProfile(userId, headers);

		userProfile = UserHelper.extractUserFromJson(bsbUserProfile, userId);

		// Decide flag based on userType
		if (userProfile.getUserType().equalsIgnoreCase("POSTPAID")) {
			userFlag = true;
		}

		return userFlag;
	}
	

	@Override
	public String getNextEpisode(String cpToken, String episodeId, String seriesId, String seasonNumber,
			String episodeNumber, String tvSeasonId, Boolean seriesFlag) {
		String response = "";
		try {
			switch (cpToken.toUpperCase()) {
			case "SONYLIV":
				response = getNextEpisodeForSonyliv(episodeId, seriesId, seasonNumber, episodeNumber, tvSeasonId,
						seriesFlag);
				break;
			case "SINGTEL":
				response = getNextEpisodeForSingtel(episodeId, seriesId, seasonNumber, episodeNumber, tvSeasonId,
						seriesFlag);
				break;
			case "YOUTUBE":
				response = getNextEpisodeForSingtel(episodeId, seriesId, seasonNumber, episodeNumber, tvSeasonId,
						seriesFlag);
				break;
			}
		} catch (BusinessApplicationException e) {
			log.error("Next Episode Error Log -", e);
			throw new BusinessApplicationException(HttpStatus.BAD_REQUEST.value(), "Series ID Missing!");
		} catch (Exception e) {
			log.error("Error, line 316 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return response;
	}

	/*** Private Methods ***/

	private String getNextEpisodeForSonyliv(String episodeId, String seriesId, String seasonNumber,
			String episodeNumber, String tvSeasonId, Boolean seriesFlag) {
		int nextEpisodeNumber = 0;

		if (!episodeNumber.isEmpty() && episodeNumber != null && !episodeNumber.equalsIgnoreCase("null")) {
			nextEpisodeNumber = Integer.parseInt(episodeNumber) + 1;
		}

		String response = "";
		if (seriesFlag) {
			String baseUrl = AppgridHelper.mpxFeedData.get("episodeslistdescending").asString();
			baseUrl = baseUrl.replace("{0}", episodeId);
			baseUrl = baseUrl.replace("{1}", "0");
			baseUrl = baseUrl.replace("{2}", "1");
			response = Util.executeApiGetCall(baseUrl);
			response = ContentProviderHelper
					.createJsonResponse(response);
		} else {
			if (episodeNumber.isEmpty() || episodeNumber == null || episodeNumber.equalsIgnoreCase("null")) {
				String baseUrl = seriesFlag ? AppgridHelper.mpxFeedData.get("episodeslistdescending").asString() + "&fields=guid" : AppgridHelper.mpxFeedData.get("episodeslistascending").asString() + "&fields=guid";
				String nextEpisodeId = "";
				String getProgramUrl = AppgridHelper.mpxFeedData.get("programbyid").asString();
				Boolean nextEpisodeFlag = false;
				if (seriesId.isEmpty() || seriesId == null || seriesId.equalsIgnoreCase("null")) {
					throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
							"Series ID is missing!");
				}
				baseUrl = baseUrl.replace("{0}", seriesId);
				baseUrl = baseUrl.replace("{1}", "0");
				baseUrl = baseUrl.replace("{2}", "10");
				response = Util.executeApiGetCall(baseUrl);
				JsonObject responseObject = JsonObject.readFrom(response);
				JsonArray entriesArray = responseObject.get("entries").asArray();
				response = null;
				responseObject = null;
				for (JsonValue entry : entriesArray) {
					if (nextEpisodeFlag) {
						nextEpisodeId = entry.asObject().get("guid").asString();
						break;
					} else {
						if (entry.asObject().get("guid").asString().equalsIgnoreCase(episodeId)) {
							nextEpisodeFlag = true;
						}
					}
				}
				getProgramUrl = getProgramUrl.replace("{0}", nextEpisodeId);
				response = Util.executeApiGetCall(getProgramUrl);
				response = ContentProviderHelper.createJsonResponse(response.toString());
			} else {
				String baseUrl = AppgridHelper.mpxFeedData.get("nextEpisodeUrl").asString() + "&fields=guid";
				String nextEpisodeId = "";
				String getProgramUrl = AppgridHelper.mpxFeedData.get("programbyid").asString();
				baseUrl = baseUrl.replace("{0}", seriesId);
				baseUrl = baseUrl.replace("{1}", tvSeasonId);
				baseUrl = baseUrl.replace("{2}", Integer.toString(nextEpisodeNumber));
				response = Util.executeApiGetCall(baseUrl);
				JsonObject responseObject = JsonObject.readFrom(response);
				JsonArray entriesArray = responseObject.get("entries").asArray();
				if (entriesArray.size() > 0) {
					nextEpisodeId = entriesArray.get(0).asObject().get("guid").asString();
					getProgramUrl = getProgramUrl.replace("{0}", nextEpisodeId);
					response = Util.executeApiGetCall(getProgramUrl);
				}
				response = ContentProviderHelper.createJsonResponse(response);
			}
		}
		return response;
	}

	private String getNextEpisodeForSingtel(String episodeId, String seriesId, String seasonNumber,
			String episodeNumber, String tvSeasonId, Boolean seriesFlag) {
		String response = "";
		int nextEpisodeNumber = 0;
		if (!episodeNumber.isEmpty() && episodeNumber != null && !episodeNumber.equalsIgnoreCase("null")) {
			nextEpisodeNumber = Integer.parseInt(episodeNumber) + 1;
		}

		if (seriesFlag) {
			String baseUrl = AppgridHelper.mpxFeedData.get("episodeslistascending").asString();
			baseUrl = baseUrl.replace("{0}", episodeId);
			baseUrl = baseUrl.replace("{1}", "0");
			baseUrl = baseUrl.replace("{2}", "1");
			response = Util.executeApiGetCall(baseUrl);
			response = ContentProviderHelper
					.createJsonResponse(response);
		} else {
			String baseUrl = AppgridHelper.mpxFeedData.get("nextEpisodeUrlForSeries").asString() + "&fields=guid";
			String nextEpisodeId = "";
			String getProgramUrl = AppgridHelper.mpxFeedData.get("programbyid").asString();
			if (seriesId.isEmpty() || seriesId == null || seriesId.equalsIgnoreCase("null")) {
				throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Series ID is missing!");
			}
			baseUrl = baseUrl.replace("{0}", seriesId);
			baseUrl = baseUrl.replace("{1}", tvSeasonId);
			baseUrl = baseUrl.replace("{2}", Integer.toString(nextEpisodeNumber));
			response = Util.executeApiGetCall(baseUrl);
			JsonObject responseObject = JsonObject.readFrom(response);
			JsonArray entriesArray = responseObject.get("entries").asArray();

			if (entriesArray.size() > 0) {
				nextEpisodeId = entriesArray.get(0).asObject().get("guid").asString();
				getProgramUrl = getProgramUrl.replace("{0}", nextEpisodeId);
				response = Util.executeApiGetCall(getProgramUrl);
			} else {
				seasonNumber = Integer.toString(Integer.parseInt(seasonNumber) + 1);
				String baseUrl2 = AppgridHelper.mpxFeedData.get("tvseasonbyseriesid").asString();
				baseUrl2 = baseUrl2.replace("{0}", seasonNumber);
				baseUrl2 = baseUrl2.replace("{1}", seriesId);
				response = Util.executeApiGetCall(baseUrl2);
				responseObject = JsonObject.readFrom(response);
				entriesArray = responseObject.get("entries").asArray();
				if (entriesArray.size() > 0) {
					tvSeasonId = entriesArray.get(0).asObject().get("id").asString();
					baseUrl = AppgridHelper.mpxFeedData.get("nextEpisodeUrlForSeries").asString() + "&fields=guid";
					baseUrl = baseUrl.replace("{0}", seriesId);
					baseUrl = baseUrl.replace("{1}", tvSeasonId);
					baseUrl = baseUrl.replace("{2}", "1");
					response = Util.executeApiGetCall(baseUrl);
					responseObject = JsonObject.readFrom(response);
					entriesArray = responseObject.get("entries").asArray();
					if (entriesArray.size() > 0) {
						nextEpisodeId = entriesArray.get(0).asObject().get("guid").asString();
						getProgramUrl = getProgramUrl.replace("{0}", nextEpisodeId);
						response = Util.executeApiGetCall(getProgramUrl);
					}
				}
			}

			response = ContentProviderHelper
					.createJsonResponse(response);
		}
		return response;
	}

	private String getProgramsList(String cpId, String categoryId, String programType, String order, String sortingKey,
			String totalPageSize, String pageSize, String pageNumber) {
		String response = "";
		String baseURL = "";
		mpxFeedJson = AppgridHelper.mpxFeedData;
		String creditsByProgramIdsUrl = mpxFeedJson.get("creditsbyprogramid").asString();
		try {
			response = ContentProviderUtil.createPagination(totalPageSize, pageSize, pageNumber, messageSource
					.getMessage(CPConstants.WYNKSTUDIO_MPX_PAGINATION_DEFAULT_START, null, "", Locale.ENGLISH),
					messageSource.getMessage(CPConstants.WYNKSTUDIO_MPX_PAGINATION_DEFAULT_END, null, "",
							Locale.ENGLISH));
			String[] range = response != null ? !response.isEmpty() ? response.trim().split("_") : null : null;
			if (range != null && range.length > 0) {
				baseURL = getProgramsBaseUrlFromJson(cpId, categoryId, programType, sortingKey);
				baseURL = baseURL.replace("{0}", Integer.parseInt(range[0]) > 10000 ? "910" : range[0]);
				baseURL = baseURL.replace("{1}", Integer.parseInt(range[1]) > 10000 ? "1000" : range[1]);
				baseURL = baseURL.replace("{2}", categoryId != null ? categoryId : "");
				response = Util.executeApiGetCall(baseURL);
				response = ContentProviderUtil
						.getProgramWithAvailableCredits(response, creditsByProgramIdsUrl, headers);
				response = convertJsonToProgramModel(response);
			} else {
				response = messageSource
						.getMessage(CPConstants.WYNKSTUDIO_MPX_LIMIT_OVERFLOW, null, "", Locale.ENGLISH);
			}
		} catch (HttpClientErrorException e) {
			log.error("Error, line 522 -", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (Exception e) {
			log.error("Error, line 525 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occurred!");
		}
		return response;
	}

	private String getProgramsBaseUrlFromJson(String cpId, String categoryId, String programType, String sortingKey) {
		String baseUrl = "";
		String feedUrl = "";
		try {
			mpxFeedJson = AppgridHelper.mpxFeedData;
			JsonObject contentProviderJsonObject = mpxFeedJson.get(cpId).asObject();
			if (contentProviderJsonObject != null) {
				if (categoryId != null
						&& contentProviderJsonObject.get("all").asObject().get(categoryId.toLowerCase()) != null
						&& !contentProviderJsonObject.get("all").asObject().get(categoryId.toLowerCase()).isNull()) {

					if (sortingKey == null || sortingKey.isEmpty()) {
						feedUrl = contentProviderJsonObject.get("all").asObject().get(categoryId.toLowerCase())
								.asString();
						sortingKey = "";
					} else {
						feedUrl = contentProviderJsonObject.get("all").asObject()
								.get(categoryId.toLowerCase() + "_sorted").asString();
					}

					switch (sortingKey.toLowerCase()) {
					case "title":
						feedUrl = feedUrl.replace("{4}", sortingKey + "|asc");
						break;
					case "rating":
						feedUrl = feedUrl.replace("{4}", ":imdbRating|desc");
						break;
					default:
						feedUrl = feedUrl.replace("&sort={4}", "");
						break;
					}
					return feedUrl;
				} else {
					if (programType != null && !programType.isEmpty()) {
						if (programType.equalsIgnoreCase("all")) {
							JsonObject allJsonObject = contentProviderJsonObject.get("all").asObject();
							return getBaseUrlBySortingKey(sortingKey, allJsonObject);
						} else {
							JsonObject programTypeJsonObject = contentProviderJsonObject.get(programType).asObject();
							if (programTypeJsonObject != null) {
								return getBaseUrlBySortingKey(sortingKey, programTypeJsonObject);
							}
						}

					} else {
						JsonObject allJsonObject = contentProviderJsonObject.get("all").asObject();
						return getBaseUrlBySortingKey(sortingKey, allJsonObject);
					}
				}
			} else {
				return baseUrl;
			}
		} catch (Exception e) {
			log.error("Error, line 584 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occurred!");
		}
		return baseUrl;
	}

	private String getProgramsBaseUrlById() {
		String baseUrl = "";
		try {
			mpxFeedJson = AppgridHelper.mpxFeedData;
			if (mpxFeedJson != null) {
				return mpxFeedJson.get("programbyid").asString();
			}

		} catch (Exception e) {
			log.error("Error, line 599 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occurred!");
		}
		return baseUrl;
	}

	private String getBannerBaseUrlByCpId(String cpId) {
		String baseUrl = "";
		try {
			mpxFeedJson = AppgridHelper.mpxFeedData;
			if (mpxFeedJson != null) {
				JsonObject contentProviderJsonObject = mpxFeedJson.get(cpId).asObject();
				return contentProviderJsonObject.get("banner").asString();
			}

		} catch (Exception e) {
			log.error("Error, line 615 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occurred!");

		}
		return baseUrl;
	}

	/**
	 * @param sortingKey
	 * @param allJsonObject
	 * @return
	 */
	private String getBaseUrlBySortingKey(String sortingKey, JsonObject allJsonObject) {
		String baseUrl;
		if (sortingKey != null && !sortingKey.isEmpty()) {
			baseUrl = (allJsonObject.get(sortingKey) != null && !allJsonObject.get(sortingKey).isNull()) ? allJsonObject
					.get(sortingKey).asString() : allJsonObject.get("default").asString();
		} else {
			baseUrl = allJsonObject.get("default").asString();
		}
		return baseUrl;
	}

	private String getProgramByGuid(String guid) {
		String response = "";
		String baseURL = getProgramsBaseUrlById();
		try {
			baseURL = baseURL.replace("{0}", guid);
			response = Util.executeApiGetCall(baseURL);
			response = convertJsonToProgramModel(response);
			JsonObject responseJson = JsonObject.readFrom(response);
			JsonArray responseArray = (JsonArray) responseJson.get(messageSource.getMessage(
					CPConstants.WYNKSTUDIO_JSON_FIELD_ENTRIES, null, "", Locale.ENGLISH));
			if (responseArray != null && responseArray.size() > 0) {
				JsonObject programObject = (JsonObject) responseArray.get(0);
				response = programObject.toString();
			}
		} catch (HttpClientErrorException e) {
			log.error("Error, line 653 -", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		}
		return response;
	}

	private String convertJsonToProgramModel(String response) {
		mpxFeedJson = AppgridHelper.mpxFeedData;
		return ContentProviderHelper.createJsonResponse(response);
	}

	private String getCategoryOrGenre(JsonArray tagsJsonArray) {
		String category = null;
		if (tagsJsonArray
				.toString()
				.toLowerCase()
				.contains(
						messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_YOUTUBE, null, "", Locale.ENGLISH)
								.toLowerCase())
				|| tagsJsonArray
						.toString()
						.toLowerCase()
						.contains(
								messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_DAILYMOTION, null, "",
										Locale.ENGLISH).toLowerCase())) {
			for (JsonValue tagsJsonObject : tagsJsonArray) {
				if (tagsJsonObject.asObject().get("scheme").asString().equalsIgnoreCase("category")
						&& !tagsJsonObject.asObject().get("title").asString().equalsIgnoreCase("free")) {
					category = tagsJsonObject.asObject().get("title").asString();
					break;
				}
			}

		} else {
			if (tagsJsonArray.toString().contains("genre")) {
				for (JsonValue tagsJsonObject : tagsJsonArray) {
					if (tagsJsonObject.asObject().get("scheme").asString().equalsIgnoreCase("genre")) {
						category = tagsJsonObject.asObject().get("title").asString();
						break;
					}
				}
			} else if (tagsJsonArray.toString().contains("Category")) {
				for (JsonValue tagsJsonObject : tagsJsonArray) {
					if (tagsJsonObject.asObject().get("scheme").asString().equalsIgnoreCase("category")) {
						category = tagsJsonObject.asObject().get("title").asString();
						break;
					}
				}
			}
		}
		return category;
	}

	@Override
	public byte[] getAssets(String dpi) {
		switch (dpi) {
		case "hdpi":
			return AppgridHelper.assetsZipHdpi = AppgridHelper.assetsZipHdpi != null ? AppgridHelper.assetsZipHdpi
					: AppgridHelper.getZipOfAppgridAssets("hdpi");
		case "mdpi":
			return AppgridHelper.assetsZipMdpi = AppgridHelper.assetsZipMdpi != null ? AppgridHelper.assetsZipMdpi
					: AppgridHelper.getZipOfAppgridAssets("mdpi");
		case "xhdpi":
			return AppgridHelper.assetsZipXhdpi = AppgridHelper.assetsZipXhdpi != null ? AppgridHelper.assetsZipXhdpi
					: AppgridHelper.getZipOfAppgridAssets("xhdpi");
		case "xxhdpi":
			return AppgridHelper.assetsZipXxhdpi = AppgridHelper.assetsZipXxhdpi != null ? AppgridHelper.assetsZipXxhdpi
					: AppgridHelper.getZipOfAppgridAssets("xxhdpi");
		case "xxxhdpi":
			return AppgridHelper.assetsZipXxhdpi = AppgridHelper.assetsZipXxhdpi != null ? AppgridHelper.assetsZipXxhdpi
					: AppgridHelper.getZipOfAppgridAssets("xxhdpi");
		case "ipad":
			return AppgridHelper.assetsIosIpad = AppgridHelper.assetsIosIpad != null ? AppgridHelper.assetsIosIpad
					: AppgridHelper.getZipOfAppgridIosAssets(dpi, headers);
		case "iphone":
			return AppgridHelper.assetsIosIphone = AppgridHelper.assetsIosIphone != null ? AppgridHelper.assetsIosIphone
					: AppgridHelper.getZipOfAppgridIosAssets(dpi, headers);
		default:
			return AppgridHelper.assetsZipXxhdpi = AppgridHelper.assetsZipXxhdpi != null ? AppgridHelper.assetsZipXxhdpi
					: AppgridHelper.getZipOfAppgridAssets("xxhdpi");
		}
	}
	
	
	@Override
    public byte[] getAssetsForNewVersion(String dpi) {
		switch (dpi) {
		case "hdpi":
			return AppgridHelper.newAssetsZipHdpi = AppgridHelper.newAssetsZipHdpi != null ? AppgridHelper.newAssetsZipHdpi
					: AppgridHelper.getZipOfAppgridAssetsForNewVersion("hdpi");
		case "mdpi":
			return AppgridHelper.newAssetsZipMdpi = AppgridHelper.newAssetsZipMdpi != null ? AppgridHelper.newAssetsZipMdpi
					: AppgridHelper.getZipOfAppgridAssetsForNewVersion("mdpi");
		case "xhdpi":
			return AppgridHelper.newAssetsZipXhdpi = AppgridHelper.newAssetsZipXhdpi != null ? AppgridHelper.newAssetsZipXhdpi
					: AppgridHelper.getZipOfAppgridAssetsForNewVersion("xhdpi");
		case "xxhdpi":
			return AppgridHelper.newAssetsZipXxhdpi = AppgridHelper.newAssetsZipXxhdpi != null ? AppgridHelper.newAssetsZipXxhdpi
					: AppgridHelper.getZipOfAppgridAssetsForNewVersion("xxhdpi");
		case "xxxhdpi":
			return AppgridHelper.newAssetsZipXxhdpi = AppgridHelper.newAssetsZipXxhdpi != null ? AppgridHelper.newAssetsZipXxhdpi
					: AppgridHelper.getZipOfAppgridAssetsForNewVersion("xxhdpi");
		case "ipad":
			return AppgridHelper.newAssetsIosIpad = AppgridHelper.newAssetsIosIpad != null ? AppgridHelper.newAssetsIosIpad
					: AppgridHelper.getZipOfAppgridIosAssetsForNewVersion(dpi, headers);
		case "iphone":
			return AppgridHelper.newAssetsIosIphone = AppgridHelper.newAssetsIosIphone != null ? AppgridHelper.newAssetsIosIphone
					: AppgridHelper.getZipOfAppgridIosAssetsForNewVersion(dpi, headers);
		default:
			return AppgridHelper.newAssetsZipXxhdpi = AppgridHelper.newAssetsZipXxhdpi != null ? AppgridHelper.newAssetsZipXxhdpi
					: AppgridHelper.getZipOfAppgridAssetsForNewVersion("xxhdpi");
		}
	}


	@Override
	public String updateAssets() {
		headers.set("Cache-Control", "no-cache");
		AppgridHelper.updateAppgridAssets(headers);
		AppgridHelper.updateAppgridAssetsForNewVersion(headers);
		log.info("Appgrid Assets Android:" + AppgridHelper.appGridAssets.toString());
		AppgridHelper.updateAppgridAssetsIos(headers);
		AppgridHelper.updateAppgridAssetsIosForNewVersion(headers);
		log.info("Appgrid Assets iOs:" + AppgridHelper.appGridAssets.toString());
		return "success";
	}

	@Override
    @SuppressWarnings("static-access")
	public String refreshAssets() {
		JsonArray assetsJsonArray = new JsonArray().readFrom(AppgridHelper.appGridMetadata.get("server_refresh_api")
				.asObject().get("refresh_assets").asString());
		for (int i = 0; i < assetsJsonArray.size(); i++) {
			Util.executeApiGetCall(assetsJsonArray.get(i).asString(), headers);
		}
		return "success";
	}

	@Override
	public String getAllAssets() {
		return AppgridHelper.getAllAssets(headers);
	}

}
