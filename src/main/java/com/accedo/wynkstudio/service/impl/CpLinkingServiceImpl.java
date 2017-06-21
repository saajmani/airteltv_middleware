package com.accedo.wynkstudio.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.accedo.wynkstudio.dao.ProductDao;
import com.accedo.wynkstudio.dao.RecentDao;
import com.accedo.wynkstudio.dao.UserProfileDao;
import com.accedo.wynkstudio.exception.BusinessApplicationException;
import com.accedo.wynkstudio.helper.AppgridHelper;
import com.accedo.wynkstudio.helper.SubscriptionHelper;
import com.accedo.wynkstudio.service.CpLinkingService;
import com.accedo.wynkstudio.util.OAuth;
import com.accedo.wynkstudio.util.Util;
import com.accedo.wynkstudio.vo.ProductVO;
import com.accedo.wynkstudio.vo.RecentVO;
import com.accedo.wynkstudio.vo.UserProfileVO;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

@Service
public class CpLinkingServiceImpl implements CpLinkingService {

	@Autowired
	private UserProfileDao userProfileDao;

	@Autowired
	private RecentDao recentDao;

	@Autowired
	private ProductDao productDao;

	private HttpHeaders headers;
	final Logger log = LoggerFactory.getLogger(this.getClass());

	@PostConstruct
	public void init() {
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	}

	@Override
	public String getNewProfiles(String cpToken, String contentId, String token, String uid) {
		String result = "";
		UserProfileVO userProfileVO = userProfileDao.getUserLoginDetails(uid);
		if (userProfileVO != null) {
			if (cpToken.equalsIgnoreCase("erosnow")) {
				result = getErosProfiles(contentId, token, uid);
			} else {
				result = getMPXProfiles(contentId, token, uid, cpToken);
			}
		} else {
			log.error("Error, line 68 - User Not Found!");
			throw new BusinessApplicationException(HttpStatus.NOT_FOUND.value(), "User Not Found");
		}

		return result;
	}

	private String getMPXProfiles(String releaseUrl, String token, String uid, String cpId) {
		String response = "";
		String streamUrl = "";
		String lastWatchedTime = "";
		String contentId = "";

		try {
			response = checkEntitlement(releaseUrl, token);
			streamUrl = response.split("&guid:")[0];
			contentId = response.split("&guid:")[1];
			if (response != null && !response.isEmpty()) {
				JsonObject rspJson = new JsonObject();
				lastWatchedTime = getLastWatchedTime(contentId, uid);
				cpId = cpId.replace("PROMOCP", "YOUTUBE");
				String responseJson = AppgridHelper.appGridMetadata.get("cp_playback_profiles").asObject()
						.get(cpId.toUpperCase()).asString();
				rspJson.add("statusCode", HttpStatus.OK.value());
				if (responseJson.contains("sdk")) {
					responseJson = responseJson.replace("{0}", contentId.replace(cpId.toUpperCase() + "_", "").trim());
					if(cpId.equalsIgnoreCase("YOUTUBE") && contentId.contains("__") ){
						responseJson = responseJson.replace("_episode_", "").trim();
					}
				} else {
					responseJson = responseJson.replace("{0}", streamUrl);
				}
				rspJson.add("statusCode", HttpStatus.OK.value());
				if (lastWatchedTime != null) {
					responseJson = responseJson.replace("{1}", lastWatchedTime);
				} else {
					responseJson = responseJson.replace("{1}", "");
				}
				responseJson = responseJson.replace("{2}", contentId);
				rspJson.add("responseBody", responseJson);

				// Provision Evergent product if validity expires
				if (cpId.equalsIgnoreCase("SINGTEL")) {
					UserProfileVO userProfileVO = userProfileDao.getUserLoginDetails(uid);
					List<ProductVO> subscribedChannels = userProfileVO.getSubscribedChannels();
					long productValidity = 0;
					String product = "";
					if (subscribedChannels != null && subscribedChannels.size() > 0) {
						for (int i = 0; i < subscribedChannels.size(); i++) {
							if (subscribedChannels.get(i).getCpId().equalsIgnoreCase("SINGTEL")
									&& subscribedChannels.get(i).getLive() == true) {
								productValidity = subscribedChannels.get(i).getContentValidity();
								product = subscribedChannels.get(i).getProductId();
								if (!product.isEmpty()
										&& (!SubscriptionHelper.checkHooqPackStatus(uid, headers) || (!subscribedChannels
												.get(i).getActive() || productValidity == 0))
										&& checkLitePack(product, contentId)) {
									response = SubscriptionHelper.createSingtelUser(uid, product,
											userProfileVO.getEmail(), headers);
									productDao.updateProductActivated(uid, product);
									log.info("HOOQ Product Activated in DB in cplsi for uid:"+ uid +", product:" + product+ ", validity:" + productValidity);
									long monthInMs = (long) 2592000000.00;
									productDao.updateProductValidity(uid, product, System.currentTimeMillis()
											+ monthInMs);
									productDao.updateProductActive(uid, product, true);
									if(product.equals("12908"))
									{
										userProfileDao.setHooqTrialFlag(uid);
									}
									setSingtelSubscription(uid, product, null);
								}
							}
						}
					}
				}else if (cpId.equalsIgnoreCase("SONYLIV")) {
					updateSonyLivUserProduct(uid, contentId);
				}

				response = rspJson.toString();
			} else {
				JsonObject rspJson = new JsonObject();
				rspJson.add("statusCode", HttpStatus.FORBIDDEN.value());
				rspJson.add("responseBody", "{\"status\":\"Entitlement check Failed!\"}");
				response = rspJson.toString();
			}
		} catch (Exception e) {
			log.error("Error, line 128 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		}
		return response;
	}

	private void updateSonyLivUserProduct(String uid, String originalContentId) {
		UserProfileVO userProfileVO = userProfileDao.getUserLoginDetails(uid);
		List<ProductVO> subscribedChannels = userProfileVO.getSubscribedChannels();
		long productValidity = 0;
		String product = "";

		String baseUrl = AppgridHelper.mpxFeedData.get("programbyid").asString();
		baseUrl = baseUrl.replace("{0}", originalContentId);
		String response = Util.executeApiGetCall(baseUrl + "&fields=:isFree,guid");
		JsonObject rspObj = JsonObject.readFrom(response);
		JsonObject entry = rspObj.get("entries").asArray().get(0).asObject();
		boolean isFree = entry.get("pl1$isFree").asBoolean();

		if (!isFree) {
		if (subscribedChannels != null && subscribedChannels.size() > 0) {
			for (int i = 0; i < subscribedChannels.size(); i++) {
				if (subscribedChannels.get(i).getCpId().equalsIgnoreCase("SONYLIV")
						&& subscribedChannels.get(i).getLive() == true) {
					productValidity = subscribedChannels.get(i).getContentValidity();
					product = subscribedChannels.get(i).getProductId();
					if (!product.isEmpty()
							&& ( (!subscribedChannels
									.get(i).getActive() || productValidity == 0))) {
						productDao.updateProductActivated(uid, product);
						log.info("SONYLIV Product Activated in DB in cplsi for uid:"+ uid +", product:" + product+ ", validity:" + productValidity);
						long monthInMs = (long) 2592000000.00;
						productDao.updateProductValidity(uid, product, System.currentTimeMillis()
								+ monthInMs);
						productDao.updateProductActive(uid, product, true);
					}
				}
			}
		}
		}
	}

	private boolean checkLitePack(String product, String contentId) {
		JsonObject litePackJsonObject;
		String url = null;
		String response = "";
		String productType = SubscriptionHelper.allProductsMap.get(product).asObject().get("productType").asString();
		if (productType.equalsIgnoreCase("lite")) {
			url = AppgridHelper.mpxFeedData.get("programbyid").asString()
					.replace("{0}", contentId + "&fields=:pack,guid");
			response = Util.executeApiGetCall(url);
			litePackJsonObject = JsonObject.readFrom(response);
			JsonArray entries = litePackJsonObject.get("entries").asArray();
			if (entries.size() > 0 && entries.get(0).asObject().get("pl1$pack") != null
					&& !entries.get(0).asObject().get("pl1$pack").isNull()
					&& entries.get(0).asObject().get("pl1$pack").asString().toLowerCase().contains("lite")) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	private String checkEntitlement(String releaseUrl, String token) {
		String response = "";
		try {
			String url = releaseUrl + "?format=smil&token=" + token;
			String rsp = Util.getLocationCall(url, headers);
			if (rsp != null) {
				response = rsp;
			}
		} catch (HttpClientErrorException e) {
			response = null;
		}
		return response;
	}

	private String getLastWatchedTime(String contentId, String uid) {
		String response = null;
		List<RecentVO> recentVOs = recentDao.getRecentListUserId(uid);
		for (RecentVO recentVO : recentVOs) {
			if (recentVO.getAssetId().equalsIgnoreCase(contentId)) {
				response = recentVO.getLastWatchedPosition();
			}
		}
		return response != null ? response : getLastWatchedTimeByContentId(contentId);
	}

	private String getErosProfiles(String releaseUrl, String token, String uid) {
		String response = "";
		String lastWatchedTime = "";
		String streamUrl = "";
		JsonValue streamUrlValue = null;
		String contentId = "";
		String erosString = "EROSNOW_";
		try {
			response = checkEntitlement(releaseUrl, token);
			if (response != null && !response.isEmpty()) {
				streamUrl = response.split("&guid:")[0];
				contentId = response.split("&guid:")[1];
				response = getUrlFromEros(contentId.replace(erosString, "").trim(), uid, 0);
				if (JsonObject.readFrom(response).get("statusCode").asInt() == 200) {
					if (JsonObject.readFrom(JsonObject.readFrom(response).get("responseBody").asString())
							.get("profiles").asObject().get("ADAPTIVE_ALL") == null) {
						streamUrlValue = JsonObject
								.readFrom(JsonObject.readFrom(response).get("responseBody").asString()).get("profiles")
								.asObject().get("ADAPTIVE_SD").asArray().get(0).asObject().get("url");
					} else {
						streamUrlValue = JsonObject
								.readFrom(JsonObject.readFrom(response).get("responseBody").asString()).get("profiles")
								.asObject().get("ADAPTIVE_ALL").asArray().get(0).asObject().get("url");
					}
					if (streamUrlValue != null && !streamUrlValue.isNull()) {
						streamUrl = streamUrlValue.asString();
					} else {
						streamUrl = JsonObject.readFrom(JsonObject.readFrom(response).get("responseBody").asString())
								.get("profiles").asObject().get("ADAPTIVE_ALL").asArray().get(0).asObject().get("url")
								.asString();
					}
					JsonObject rspJson = new JsonObject();
					lastWatchedTime = getLastWatchedTime(contentId, uid);
					String responseJson = AppgridHelper.appGridMetadata.get("cp_playback_profiles").asObject()
							.get("EROSNOW").asString();
					if (responseJson.contains("sdk")) {
						responseJson = responseJson.replace("{0}", contentId.replace(erosString, "").trim());
					} else {
						responseJson = responseJson.replace("{0}", streamUrl);
					}
					rspJson.add("statusCode", HttpStatus.OK.value());
					if (lastWatchedTime != null) {
						responseJson = responseJson.replace("{1}", lastWatchedTime);
					} else {
						responseJson = responseJson.replace("{1}", "");
					}
					responseJson = responseJson.replace("{2}", contentId);
					rspJson.add("responseBody", responseJson);
					response = rspJson.toString();
				}
			} else {
				JsonObject rspJson = new JsonObject();
				rspJson.add("statusCode", HttpStatus.FORBIDDEN.value());
				rspJson.add("responseBody", "{\"status\":\"Entitlement check Failed!\"}");
				response = rspJson.toString();
			}
		} catch (Exception e) {
			log.error("Exception in Get Streaming Profile, line 216: ", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		}
		return response;
	}

	public String getUrlFromEros(String contentId, String uid, int count) throws ParseException {
		String platform = "2";
		String profileUrl = AppgridHelper.mpxFeedData.get("erosnowprofiles").asString();
		String result = "";
		String originalContentId = contentId;
		UserProfileVO userProfileVO = userProfileDao.getUserLoginDetails(uid);
		if (userProfileVO != null && userProfileVO.getErosnowUserToken() != null
				&& !userProfileVO.getErosnowUserToken().isEmpty()) {
			contentId = (contentId.replace("EROS_", "")).trim();
			contentId = (contentId.replace("EROSNOW_", "")).trim();
			profileUrl = profileUrl + contentId;
			profileUrl = profileUrl + "?error=true&country=IN&quality=auto&platform=" + platform;

			String baseUrl = AppgridHelper.mpxFeedData.get("programbyid").asString();
			baseUrl = baseUrl.replace("{0}", originalContentId);
			String response = Util.executeApiGetCall(baseUrl + "&fields=:isFree,guid");
			JsonObject rspObj = JsonObject.readFrom(response);
			JsonObject entry = rspObj.get("entries").asArray().get(0).asObject();
			boolean isFree = entry.get("pl1$isFree").asBoolean();

			if (!isFree) {
				List<ProductVO> currentLiveProduct = productDao.getProductsByUserId(uid);
				for (int i = 0; currentLiveProduct.size() > i; i++) {
					ProductVO prod = currentLiveProduct.get(i);
					if (prod.getCpId().equalsIgnoreCase("EROSNOW") && prod.getLive() && !prod.getActive()) {
						JsonObject planObject = JsonObject.readFrom(AppgridHelper.appGridMetadata.get(
								"erosnow_product_plan").asString());
						String plan = planObject.get(prod.getProductId()).asString();
						String rsp = "";
						try {
							rsp = purchase(uid, plan, userProfileVO);
							if (JsonObject.readFrom(rsp).get("statusCode").asInt() == 200) {
								productDao.updateProductActivated(uid, prod.getProductId());
								cancelPurchase(uid, plan, userProfileVO);
							}
						} catch (BusinessApplicationException e) {
							log.error("Erosnow Purchase Failed in Pack Over-writing scenario!");
						}
						productDao.updateProductActive(uid, prod.getProductId(), true);

						if (prod.getProductType().equalsIgnoreCase("lite")) {
							long monthInMs = 0;
							int productCycle = SubscriptionHelper.allProductsMap.get(prod.getProductId()).asObject()
									.get("productCycle").asInt();
							if (productCycle == 1) {
								monthInMs = (long) 86400000.00;
							} else if (productCycle == 7) {
								monthInMs = (long) 604800000.00;
							} else {
								monthInMs = (long) 2592000000.00;
							}

							productDao.updateProductValidity(uid, prod.getProductId(), System.currentTimeMillis()
									+ monthInMs);
						}

					}
				}
			}

			result = OAuth.OAuthRequestToken("GET", userProfileVO.getErosnowUserToken(),
					userProfileVO.getErosnowUserTokenSecret(), profileUrl, null, "");
			JsonObject json = JsonObject.readFrom(result);
			if (json.get("statusCode").asInt() == 409 || json.get("statusCode").asInt() == 401) {
				result = loginErosnowUser(userProfileVO.getEmail(), headers, uid);
				json = JsonObject.readFrom(result);
				if (json.get("statusCode").asInt() == 200) {
					if (count < 3) {
						count++;
						return getUrlFromEros(originalContentId, uid, count);
					}
				}
			} else if (json.get("statusCode").asInt() == 403) {
				if (userProfileVO.getSubscribedChannels() != null && userProfileVO.getSubscribedChannels().size() > 0) {
					String planId = "";
					for (int i = 0; i < userProfileVO.getSubscribedChannels().size(); i++) {
						if (userProfileVO.getSubscribedChannels().get(i).getCpId().equalsIgnoreCase("EROSNOW")
								&& userProfileVO.getSubscribedChannels().get(i).isAllowPlayback()) {
							planId = userProfileVO.getSubscribedChannels().get(i).getProductId();
						}
					}
					if (!planId.isEmpty()) {
						JsonObject planObject = JsonObject.readFrom(AppgridHelper.appGridMetadata.get(
								"erosnow_product_plan").asString());
						String plan = planObject.get(planId).asString();
						String rsp = purchase(uid, plan, userProfileVO);
						productDao.updateProductActive(uid, planId, true);

						long monthInMs = 0;
						int productCycle = SubscriptionHelper.allProductsMap.get(planId).asObject().get("productCycle")
								.asInt();
						if (productCycle == 1) {
							monthInMs = (long) 86400000.00;
						} else if (productCycle == 7) {
							monthInMs = (long) 604800000.00;
						} else {
							monthInMs = (long) 2592000000.00;
						}

						productDao.updateProductValidity(uid, planId, System.currentTimeMillis() + monthInMs);

						if (JsonObject.readFrom(rsp).get("statusCode").asInt() == 200) {
							productDao.updateProductActivated(uid, planId);
							cancelPurchase(uid, plan, userProfileVO);
							if (count < 3) {
								count++;
								return getUrlFromEros(originalContentId, uid, count);
							}
						}

					} else {
						log.error("User Not Premium - ", json.get("responseBody").asString());
						throw new BusinessApplicationException(HttpStatus.FORBIDDEN.value(),
								"User Not subscribed to Play premium Erosnow Content!");
					}
				}
			}
		} else {
			result = createErosnowUser(userProfileVO.getEmail(), headers, uid);
			JsonObject json = JsonObject.readFrom(result);
			if (json.get("statusCode").asInt() == 200) {
				if (count < 3) {
					count++;
					return getUrlFromEros(originalContentId, uid, count);
				}
			} else {
				log.error("EROS User Creation failed - ", json.get("responseBody").asString());
				throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Eros User Creation Failed!");
			}
		}
		return result;
	}

	private String getLastWatchedTimeByContentId(String contentId){
		String seconds = null;
		String baseUrl = AppgridHelper.mpxFeedData.get("programbyid").asString();
		baseUrl = baseUrl.replace("{0}", contentId);
		String response = Util.executeApiGetCall(baseUrl);
		JsonObject rspObj = JsonObject.readFrom(response);
		if(rspObj.get("entries").asArray().size() > 0){
			JsonObject entry = rspObj.get("entries").asArray().get(0).asObject();
				String duration[] = entry.get("pl1$startDuration") != null && !entry
						.get("pl1$startDuration").isNull() ? entry.get("pl1$startDuration").asString().split(":") : null;
				if(duration != null && duration.length==3){
					seconds = String.valueOf((Integer.parseInt(duration[0]) * 3600) + (Integer.parseInt(duration[1]) * 60) + Integer.parseInt(duration[2])); 
				}
			 
			
		}
		return seconds; 
		 
	}
	private String createErosnowUser(String email, HttpHeaders headers, String userId) throws ParseException {
		JsonObject mpxFeedJson = AppgridHelper.mpxFeedData;
		JsonObject eroscredentials = new JsonObject();
		String userName = email + "." + userId + ".airtel.wynk.erosnow";
		eroscredentials.add("email", userName);
		String partnerId = userId + "-airtelwynkerosnow";
		eroscredentials.add("partnerid", partnerId);
		String loginResponse = OAuth.OAuthRequestToken("POST", "", "", mpxFeedJson.get("erosnow_register").asString(),
				eroscredentials.toString(), "register");
		JsonObject jsonObject = JsonObject.readFrom(loginResponse);
		Long code = jsonObject.get("statusCode").asLong();
		if (code == 200) {
			saveErosCredentials(userId, email, JsonObject.readFrom(jsonObject.get("responseBody").asString()));
		} else if (code == 409) {
			loginResponse = loginErosnowUser(email, headers, userId);
		}
		return loginResponse;
	}

	private String loginErosnowUser(String email, HttpHeaders headers, String userId) throws ParseException {
		String loginResponse = null;
		try {
			String userName = email + "." + userId + ".airtel.wynk.erosnow";
			JsonObject mpxFeedJson = AppgridHelper.mpxFeedData;
			JsonObject eroscredentials = new JsonObject();
			eroscredentials.add("email", userName);
			String partnerId = userId + "-airtelwynkerosnow";
			eroscredentials.add("partnerid", partnerId);
			loginResponse = OAuth.OAuthRequestToken("POST", "", "", mpxFeedJson.get("erosnowlogin").asString(),
					eroscredentials.toString(), "login");
			JsonObject jsonObject = JsonObject.readFrom(loginResponse);
			Long code = jsonObject.get("statusCode").asLong();
			if (code == 200) {
				saveErosCredentials(userId, userName, JsonObject.readFrom(jsonObject.get("responseBody").asString()));
			} else {
				saveErosCredentials(userId, userName, JsonObject.readFrom(jsonObject.get("responseBody").asString()));
				throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Erosnow login failed!");
			}
		} catch (Exception e) {
			log.error("Error, line 410 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return loginResponse;
	}

	public String saveErosCredentials(String userId, String loginUserName, JsonObject jsonObject) {
		String token = jsonObject.get("token").asString();
		String token_secret = jsonObject.get("token_secret").asString();

		return userProfileDao.updateUserLoginDetailse(userId, loginUserName, token, token_secret);
	}

	public String provisionErosnowProduct(String token, String tokenSecret, JsonObject userInfoJson, HttpHeaders headers) {
		JsonObject mpxFeedJson = AppgridHelper.mpxFeedData;
		String loginResponse = OAuth.OAuthRequestToken("POST", token, tokenSecret, mpxFeedJson.get("erosnow_purchase")
				.asString(), userInfoJson.toString(), "purchase");
		return loginResponse;
	}

	public String cancelErosnowProduct(String token, String tokenSecret, JsonObject userInfoJson, HttpHeaders headers) {
		JsonObject mpxFeedJson = AppgridHelper.mpxFeedData;
		String loginResponse = OAuth.OAuthRequestToken("POST", token, tokenSecret, mpxFeedJson.get("erosnow_cancel")
				.asString(), userInfoJson.toString(), "cancel");
		return loginResponse;
	}

	public String purchase(String uid, String plan, UserProfileVO userProfileVO) {
		String purchaseResponse = null;
		try {
			if (userProfileVO != null && userProfileVO.getErosnowUserToken() != null
					&& !userProfileVO.getErosnowUserToken().isEmpty()) {
				JsonObject eroscredentials = new JsonObject();
				eroscredentials.add("email", userProfileVO.getEmail() + "." + uid + ".airtel.wynk.erosnow");
				eroscredentials.add("partnerid", uid + "-airtelwynkerosnow");
				eroscredentials.add("plan", plan);
				purchaseResponse = provisionErosnowProduct(userProfileVO.getErosnowUserToken(),
						userProfileVO.getErosnowUserTokenSecret(), eroscredentials, headers);
				JsonObject json = JsonObject.readFrom(purchaseResponse);
				if (json.get("statusCode").asInt() == 409 || json.get("statusCode").asInt() == 401) {
					String result = loginErosnowUser(userProfileVO.getEmail(), headers, uid);
					json = JsonObject.readFrom(result);
					if (json.get("statusCode").asInt() == 200) {
						purchase(uid, plan, userProfileVO);
					}
				} else if (json.get("statusCode").asInt() == 200) {
					log.info("Eros Purchase Done For User: " + uid + ", Plan:" + plan);
					purchaseResponse = "{\"statusCode\":200, \"message\":\"Purchase Successful\"}";
				} else {
					log.error("EROS Purchase Failed - ", json.get("responseBody").asString());
					purchaseResponse = "{\"statusCode\":400, \"message\":\"Purchase Unsuccessful\"}";
				}
			} else {
				String result = createErosnowUser(userProfileVO.getEmail(), headers, uid);
				JsonObject json = JsonObject.readFrom(result);
				if (json.get("statusCode").asInt() == 200) {
					return purchase(uid, plan, userProfileVO);
				} else {
					log.error("EROS User Creation failed - ", json.get("responseBody").asString());
					throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
							"Eros User Creation Failed!");
				}
			}
		} catch (Exception e) {
			log.error("Error, line 478 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return purchaseResponse;
	}

	public String cancelPurchase(String uid, String plan, UserProfileVO userProfileVO) {
		JsonObject eroscredentials = new JsonObject();
		eroscredentials.add("email", userProfileVO.getEmail() + "." + uid + ".airtel.wynk.erosnow");
		eroscredentials.add("partnerid", uid + "-airtelwynkerosnow");
		eroscredentials.add("plan", plan);
		String cancelResponse = cancelErosnowProduct(userProfileVO.getErosnowUserToken(),
				userProfileVO.getErosnowUserTokenSecret(), eroscredentials, headers);
		JsonObject json = JsonObject.readFrom(cancelResponse);
		return json.toString();
	}

	private Boolean setSingtelSubscription(String userId, String SKU, JsonObject statusObject) {
		userProfileDao.updateUserProfileCreatedflag(userId, true);
		return true;
	}
}
