package com.accedo.wynkstudio.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.accedo.wynkstudio.common.CPConstants;
import com.accedo.wynkstudio.dao.BundleCounterDao;
import com.accedo.wynkstudio.dao.FavouriteDao;
import com.accedo.wynkstudio.dao.OfferDao;
import com.accedo.wynkstudio.dao.PersonalizedRailDao;
import com.accedo.wynkstudio.dao.ProductDao;
import com.accedo.wynkstudio.dao.RecentDao;
import com.accedo.wynkstudio.dao.UserProfileDao;
import com.accedo.wynkstudio.entity.BundleCounter;
import com.accedo.wynkstudio.entity.Favourite;
import com.accedo.wynkstudio.entity.Offer;
import com.accedo.wynkstudio.entity.PersonalizedRail;
import com.accedo.wynkstudio.entity.Product;
import com.accedo.wynkstudio.entity.Recent;
import com.accedo.wynkstudio.entity.UserProfile;
import com.accedo.wynkstudio.exception.BusinessApplicationException;
import com.accedo.wynkstudio.helper.AppgridHelper;
import com.accedo.wynkstudio.helper.ContentProviderHelper;
import com.accedo.wynkstudio.helper.ProducttHelper;
import com.accedo.wynkstudio.helper.SubscriptionHelper;
import com.accedo.wynkstudio.mongodb.entity.User;
import com.accedo.wynkstudio.mongodb.entity.UserDerivedProfile;
import com.accedo.wynkstudio.service.UserService;
import com.accedo.wynkstudio.util.ContentProviderUtil;
import com.accedo.wynkstudio.util.TrustedAuth;
import com.accedo.wynkstudio.util.Util;
import com.accedo.wynkstudio.vo.AppgridVO;
import com.accedo.wynkstudio.vo.BundleCounterVO;
import com.accedo.wynkstudio.vo.FavouriteVO;
import com.accedo.wynkstudio.vo.OfferVO;
import com.accedo.wynkstudio.vo.PersonalizedRailVO;
import com.accedo.wynkstudio.vo.ProductVO;
import com.accedo.wynkstudio.vo.RecentVO;
import com.accedo.wynkstudio.vo.UserProfileVO;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonObject.Member;
import com.eclipsesource.json.JsonValue;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private UserProfileDao userProfileDao;

	@Autowired
	private RecentDao recentDao;

	@Autowired
	private FavouriteDao favouriteDao;
	@Autowired
	private ProductDao productDao;

	@Autowired
	private OfferDao offerDao;

//	@Autowired
//	private MongoDBUserDAO mongoDBUserDAO;

	@Autowired
	private PersonalizedRailDao personalizedRailDao;

//	@Autowired
//	private MongoDBUserDerivedProfileDAO mongoDBUserDerivedProfileDAO;

	@Autowired
	private BundleCounterDao bundleCounterDao;

	private HttpHeaders headers;
	private JsonObject appGridMetadata;
	final Logger log = LoggerFactory.getLogger(this.getClass());

	@PostConstruct
	public void init() {
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		AppgridHelper.getSession();
		appGridMetadata = updateMetadata();
	}

	/*** Public Methods ***/

	/* Update Appgrid meta-data for middleware */
	@Override
	public JsonObject getMetadata() {
		// AppgridHelper.updateAppgridAssets(headers);
		return updateMetadata();
	}

	@Override
    public String refreshMetadata() {
		String results = "";
		JsonArray assetsJsonArray = JsonArray.readFrom(AppgridHelper.appGridMetadata.get("server_refresh_api")
				.asObject().get("refresh_metadata").asString());
		for (int i = 0; i < assetsJsonArray.size(); i++) {
			results = Util.executeApiGetCall(assetsJsonArray.get(i).asString(), headers);
		}
		return results;
	}

	private JsonObject updateMetadata() {
		appGridMetadata = AppgridHelper.updateMetadata(headers);
		return appGridMetadata;
	}

	/* Get User Profile from AppGrid */
	@Override
	public String getUserById(String userId, String contextPath, String token, String deviceId, String platform, String appVersion) {
		String response = "";
		JsonObject mpxJson = new JsonObject();
		ObjectMapper mapper = new ObjectMapper();
		String mpxToken = "";
		try {
			String bsbResponse = SubscriptionHelper.checkPackStatus(userId, token, headers);
			
			UserProfileVO userProfileVO = userProfileDao.getUserProfileByUserId(userId);
			if (userProfileVO == null) {
				response = "{\"statusCode\":404,\"message\":\"User Not Found\"}";
			} else {
				if (token != null && !token.isEmpty() && token != "null") {
					String tokenJson = "{\"token\":\"" + token + "\"}";
					mpxJson = JsonObject.readFrom(getUserTokenFromMPX(userId, contextPath, tokenJson, bsbResponse));
					mpxToken = mpxJson.get("signInResponse").asObject().get("token").asString();
				} else {
					mpxJson = JsonObject.readFrom(getUserTokenFromMPX(userId, contextPath, "", ""));
					mpxToken = mpxJson.get("signInResponse").asObject().get("token").asString();
				}
				userProfileVO = userProfileDao.getUserProfileByUserId(userId);
                                //Adding Airtel infinity pack for an Airtel postpaid user
                                JsonObject airtelProduct = null;
                                JsonObject statusObject = JsonObject.readFrom(bsbResponse);
                                String airtelOfferId = AppgridHelper.appGridMetadata.get("gift_products_def").asObject().get("livetv_single_prod_id").asString();
                                if (statusObject.get(airtelOfferId) != null
                                        && statusObject.get(airtelOfferId).asObject().get("status").asString().equalsIgnoreCase("DEACTIVATED")) {
                                    airtelProduct = SubscriptionHelper.allProductsMap.get(airtelOfferId);
                                    JsonObject offerObj = statusObject.get(airtelOfferId).asObject();
                                    long validity = offerObj.get("expireTimestamp").asLong();
                                    airtelProduct.add("validity", String.valueOf(validity));
                                    airtelProduct.set("state", offerObj.get("status").asString());
                                    airtelProduct.set("live", "true");
                                    airtelProduct.set("active", offerObj.get("status").asString().equalsIgnoreCase("ACTIVE"));
                    airtelProduct.set("cpId", airtelProduct.getString("contentProvider", "AIRTEL"));
                                } else if (!platform.isEmpty() && !appVersion.isEmpty()) {
                                    String bsbAvailableOffers = SubscriptionHelper.getavailableOffer(userId, deviceId, platform, appVersion);
                                    JsonObject offerResponse = JsonObject.readFrom(bsbAvailableOffers);
                                    if (offerResponse.get("offerStatus").asArray() != null && offerResponse.get("offerStatus").asArray().size() > 0) {
                                        JsonArray offerstatus = offerResponse.get("offerStatus").asArray();
                                        for (int i = 0; i < offerstatus.size(); i++) {
                                            if (offerstatus.get(i).asObject().get("packs").asArray() != null &&
                                                    offerstatus.get(i).asObject().get("packs").asArray().size() > 0) {
                                                JsonArray offerpacks = offerstatus.get(i).asObject().get("packs").asArray();
                                                JsonArray subPacks = new JsonArray();

                                                for (int j = 0; j < offerpacks.size(); j++) {
                                                    String offerId = offerpacks.get(j).asObject().get("partnerProductId").asString();
                                                    String action = offerpacks.get(j).asObject().get("action").asString();

                                                    if (offerId.equalsIgnoreCase(AppgridHelper.appGridMetadata.get("gift_products_def").asObject().get("livetv_single_prod_id").asString())) {
                                                        airtelProduct = airtelProduct = SubscriptionHelper.allProductsMap.get(airtelOfferId);
                                                        for (int k = 0; k < offerpacks.size(); k++) {
                                                            if(offerpacks.get(k).asObject().get("cpName") != null)
                                                                subPacks.add(offerpacks.get(k).asObject().get("cpName"));
                                                        }
                                                        if (subPacks.size() > 0) {
                                                            airtelProduct.add("subPackCpIds", subPacks);
                                                        }
                                                        if(action.equalsIgnoreCase("ACTIVE")) {
                                                            if (statusObject.get(offerId) != null) {
                                                                JsonObject offerObj = statusObject.get(offerId).asObject();
                                                                long validity = offerObj.get("expireTimestamp").asLong();
                                                                airtelProduct.add("validity", String.valueOf(validity));
                                                                airtelProduct.set("state", offerObj.get("status").asString());
                                                                airtelProduct.set("active", offerObj.get("status").asString().equalsIgnoreCase("ACTIVE"));
                                                                airtelProduct.set("noOfDaysLeft", TimeUnit.MILLISECONDS.toDays(validity) - 
                                                                        TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()));
                                                            } else {
                                                                long validity = offerpacks.get(j).asObject().get("validTillDate").asLong();
                                                                airtelProduct.add("validity", validity);
                                                                airtelProduct.set("active", "false");
                                                                airtelProduct.set("noOfDaysLeft", TimeUnit.MILLISECONDS.toDays(validity) - 
                                                                        TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()));
                                                            }
                                                        } else {
                                                            airtelProduct.set("active", "false");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
				Object json = mapper.readValue(mapper.writeValueAsString(userProfileVO), UserProfileVO.class);
				response = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
				JsonObject rspJson = JsonObject.readFrom(response);
                                if (airtelProduct != null) {
                                    JsonArray sbChannel = rspJson.get("subscribedChannels").asArray();
                                    boolean hasProd = false;
                                    for (JsonValue ch : sbChannel) {
                                        if (ch.asObject().get("productId").asString().equalsIgnoreCase(AppgridHelper.appGridMetadata.
                                                get("gift_products_def").asObject().get("livetv_single_prod_id").asString())) {
                                            hasProd = true;
                                            break;
                                        }                                            
                                    }
                                    if (!hasProd) {
                                        sbChannel.add(airtelProduct);
                                    }
                                }
				if (!mpxToken.isEmpty()) {
					rspJson.set("mpxToken", mpxToken);
				} else {
					rspJson.set("mpxToken", "");
				}

				String reorderKey = getCpReorder(userId);
				JsonValue reorderCp = AppgridHelper.appGridMetadata.get("cp_reordering").asObject().get(reorderKey);
				if (reorderCp == null || reorderCp.isNull()) {
					reorderCp = AppgridHelper.appGridMetadata.get("cp_reordering").asObject()
							.get("unknown_airtel_prepaid");
				}
				rspJson.add("reordercp", reorderCp);
				response = ContentProviderUtil.convertJsonToString(rspJson);
			}
		} catch (HttpClientErrorException e) {
			log.error("Error while making external API call, line 134- " + userId + ":", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (JsonParseException e) {
			log.error("JSON Parse Error, line 137-" + userId + ":", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e, "Some error occured!");
		} catch (JsonMappingException e) {
			log.error("Json Mapping Error, line 140- " + userId + ":", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e, "Some error occured!");
		} catch (JsonProcessingException e) {
			log.error("Json Processing Error, line 143- " + userId + ":", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e, "Some error occured!");
		} catch (IOException e) {
			log.error("IO Error, line 146- " + userId + ":", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e, "Some error occured!");
		} catch (BusinessApplicationException e) {
			log.error("User Not Found-, line 149 - " + userId + ":", e);
			throw new BusinessApplicationException(HttpStatus.NOT_FOUND.value(), e, "User Not Found!");
		} catch (Exception e) {
			log.error("Get Uer Profile Error, line 152- " + userId + ":", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e, "Some error occured!");
		}
		return response;
	}

	/* Update Profile of user in AppGrid */
	@Override
	public String setUserById(String userId, String userInfoJson) {
		String response = "";
		try {
			response = createUserById(userId, userInfoJson);
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode().value() == HttpStatus.UNAUTHORIZED.value()) {
				AppgridHelper.getSession();
				return setUserById(userId, userInfoJson);
			}
			// log.error("Create User Error- " + userId + ":", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (Exception e) {
			// log.error("Create User Error - " + userId + ":", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return response;
	}

	@Override
	public String createUserById(String userId, String userInfoJson) {
		String response = "";
		try {
			UserProfileVO userProfileVO = userProfileDao.getUserProfileByUserId(userId);
			JsonObject profileJsonObject = JsonObject.readFrom(userInfoJson);
			UserProfile userProfile = createUserProfileFromJson(userId, profileJsonObject);
			if (userProfileVO != null) {
				response = userProfileDao.updateUserProfile(userProfile);
			} else {
				response = userProfileDao.createUserProfile(userProfile);
			}
		} catch (HttpClientErrorException e) {
			// log.error("Error in creating User for User-" + userId + ":", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (Exception e) {
			// log.error("Error in creating User for User-" + userId + ":", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return response;
	}

	private UserProfile createUserProfileFromJson(String userId, JsonObject profileJsonObject) {
		UserProfile userProfile = new UserProfile();
		userProfile.setUserId(userId);
		userProfile.setGender(profileJsonObject.get("gender").asString());
		userProfile.setName(profileJsonObject.get("name").asString());
		userProfile.setDateOfBirth(profileJsonObject.get("dob").asString());
		userProfile.setEmail(profileJsonObject.get("email") != null ? profileJsonObject.get("email").asString() : null);
		return userProfile;
	}

	@Override
    public String updateUserFavouriteList(String userId, String userInfoJson) {
		String result = null;
		AppgridVO appgridVO = null;
		ObjectMapper mapper = new ObjectMapper();
		UserProfile userProfile = new UserProfile();
		Favourite favourite = new Favourite();
		try {
			if (!userInfoJson.isEmpty()) {
				appgridVO = mapper.readValue(userInfoJson, AppgridVO.class);
				userProfile.setUserId(userId);
				favourite.setUserProfile(userProfile);

				favourite.setAssetId(appgridVO.getAssetId());
				favourite.setCpToken(appgridVO.getCpToken());
				favourite.setDuration(appgridVO.getDuration());
				favourite.setDownloadedDate(appgridVO.getDownloadedDate());
				favourite.setLastWatchedPosition(appgridVO.getLastWatchedPosition());
				favourite.setLastWatchedTime(appgridVO.getLastWatchedTime());
				try {
					favouriteDao.deleteFavouriteByUserIdWithAssetId(appgridVO.getAssetId(), userId);
					if (favouriteDao.createFavourite(favourite)) {
						result = "{\"message\":\"Added to Fav List\"}";
					}
				} catch (Exception e) {
					log.error("Adding to Favourite error:" + e);
				}
			}
		} catch (Exception e) {
			log.error("Error in Updating Favorite List for User-" + userId + ":", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return result;
	}

	@Override
    public String updateUserRecentList(String userId, String userInfoJson) {
		String result = null;
		AppgridVO appgridVO = null;
		ObjectMapper mapper = new ObjectMapper();
		UserProfile userProfile = new UserProfile();
		Recent recent = new Recent();
		int recentListCount = 0;
		try {
			String recentLimit = appGridMetadata.get("recent_list_limit").asString();
			if (!userInfoJson.isEmpty()) {
				appgridVO = mapper.readValue(userInfoJson, AppgridVO.class);
				userProfile.setUserId(userId);
				recent.setUserProfile(userProfile);
				recent.setAssetId(appgridVO.getAssetId());
				recent.setCpToken(appgridVO.getCpToken());
				recent.setDuration(appgridVO.getDuration());
				recent.setDownloadedDate(appgridVO.getDownloadedDate());
				recent.setLastWatchedPosition(appgridVO.getLastWatchedPosition());
				recent.setLastWatchedTime(appgridVO.getLastWatchedTime());
				recentListCount = recentDao.getRecentListCountByUserId(userId) + 1;
				try {
					if (recentListCount > Integer.parseInt(recentLimit)) {
						recentDao.deleteFirstRecentByUserId(userId);
					}
					if (appgridVO.getLastWatchedPosition() != null && appgridVO.getDuration() != null) {
						int lastWatchedTime = Integer.parseInt(appgridVO.getLastWatchedPosition());
						int totalDuration = Integer.parseInt(appgridVO.getDuration());
						recentDao.deleteRecentByUserIdWithAssetId(appgridVO.getAssetId(), userId);
						if (lastWatchedTime + 6 <= totalDuration) {
							if (recentDao.createRecent(recent)) {
								result = "{\"message\":\"Added to Recent List\"}";
							}
						}
					}
				} catch (Exception e) {
					log.error("Adding to recent error:" + e);
				}
			}
		} catch (Exception e) {
			log.error("Error in Updating Recent List for User- Line 275: ", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e, "Some error occured!");
		}

		return result;

	}

	/* Update data for a particular key for User Profile in AppGrid */
	@Override
	public String updateUserById(String userId, String dataKey, String userInfoJson) {
		String response = "{\"code\":200,\"body\":\"Successfully Updated\"}";
		try {
			response = updateUserRecentList(userId, userInfoJson);
		} catch (HttpClientErrorException e) {
			// log.error("Error making external API call- " + userId + ":", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (Exception e) {
			// log.error("Error in Updating User- " + userId + ":", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return response;
	}

	/* Remove data for a particular key from User Profile in AppGrid */
	@Override
	public String removeAppgridDataBykey(String userId, String dataKey, String assetId) {
		String response = "{\"code\":200,\"body\":\"Successfully Deleted\"}";
		try {
			favouriteDao.deleteFavouriteByUserIdWithAssetId(assetId, userId);
		} catch (Exception e) {
			// log.error("Error in Removing fav- " + userId + ":", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return response;
	}

	@Override
    public String getGiftProductsInfo(String userId, String token, String deviceId, String deviceOs, String appVersion) {
		JsonArray productsList = new JsonArray();
		Boolean trialFlag = false;
                String bsbResponse = SubscriptionHelper.checkPackStatus(userId, token, headers);
                JsonObject statusObject = JsonObject.readFrom(bsbResponse);
                String bsbAvailableOffers = SubscriptionHelper.getavailableOffer(userId, deviceId, deviceOs, appVersion);
                JsonObject offerResponse = JsonObject.readFrom(bsbAvailableOffers);
                JsonObject airtelProduct;
                String airtelOfferId = AppgridHelper.appGridMetadata.get("gift_products_def").asObject().get("livetv_single_prod_id").asString();
                if (statusObject.get(airtelOfferId) != null
                        && statusObject.get(airtelOfferId).asObject().get("status").asString().equalsIgnoreCase("DEACTIVATED")) {
                    airtelProduct = JsonObject.readFrom(AppgridHelper.appGridMetadata.get("gift_products_def").asObject()
                                                .get("airteltv_premiumpack").asString());
                    JsonObject offerObj = statusObject.get(airtelOfferId).asObject();
                    long validity = offerObj.get("expireTimestamp").asLong();
                    airtelProduct.add("validity", String.valueOf(validity));
                    airtelProduct.set("state", offerObj.get("status").asString());
                    airtelProduct.set("active", offerObj.get("status").asString().equalsIgnoreCase("ACTIVE"));
                    productsList.add(airtelProduct);
                } else if (offerResponse.get("offerStatus").asArray() != null && offerResponse.get("offerStatus").asArray().size() > 0) {
                    JsonArray offerstatus = offerResponse.get("offerStatus").asArray();
                    for (int i = 0; i < offerstatus.size(); i++) {
                        if (offerstatus.get(i).asObject().get("packs").asArray() != null &&
                                offerstatus.get(i).asObject().get("packs").asArray().size() > 0) {
                            JsonArray offerpacks = offerstatus.get(i).asObject().get("packs").asArray();
                            JsonArray subPacks = new JsonArray();
                            
                            for (int j = 0; j < offerpacks.size(); j++) {
                                String offerId = offerpacks.get(j).asObject().get("partnerProductId").asString();
                                String action = offerpacks.get(j).asObject().get("action").asString();
                                
                                if (offerId.equalsIgnoreCase(AppgridHelper.appGridMetadata.get("gift_products_def").asObject().get("livetv_single_prod_id").asString())) {
                                    airtelProduct = JsonObject.readFrom(AppgridHelper.appGridMetadata.get("gift_products_def").asObject()
                                                .get("airteltv_premiumpack").asString());
                                    for (int k = 0; k < offerpacks.size(); k++) {
                                        if(offerpacks.get(k).asObject().get("cpName") != null)
                                            subPacks.add(offerpacks.get(k).asObject().get("cpName"));
                                    }
                                    if (subPacks.size() > 0) {
                                        airtelProduct.add("subPackCpIds", subPacks);
                                    }
                                    if(action.equalsIgnoreCase("ACTIVE")) {
                                        if (statusObject.get(offerId) != null) {
                                            JsonObject offerObj = statusObject.get(offerId).asObject();
                                            long validity = offerObj.get("expireTimestamp").asLong();
                                            airtelProduct.add("validity", String.valueOf(validity));
                                            airtelProduct.set("state", offerObj.get("status").asString());
                                            airtelProduct.set("active", offerObj.get("status").asString().equalsIgnoreCase("ACTIVE"));
                                            airtelProduct.set("noOfDaysLeft", TimeUnit.MILLISECONDS.toDays(validity) - 
                                                    TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()));
                                        } else {
                                            long validity = offerpacks.get(j).asObject().get("validTillDate").asLong();
                                            airtelProduct.add("validity", validity);
                                            airtelProduct.set("active", "false");
                                            airtelProduct.set("noOfDaysLeft", TimeUnit.MILLISECONDS.toDays(validity) - 
                                                    TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()));
                                        }
                                    } else {
                                        airtelProduct.set("active", "false");
                                    }
                                    productsList.add(airtelProduct);
                                }
                            }
                        }
                    }
                }
           
                List<ProductVO> userProducts = productDao.getProductsByUserId(userId);
		for (int i = 0; i < userProducts.size(); i++) {
			if (userProducts.get(i).getCpId().equalsIgnoreCase("hooq")
					|| userProducts.get(i).getCpId().equalsIgnoreCase("singtel")) {
				trialFlag = true;
			}
			if (!userProducts.get(i).getProductId().equalsIgnoreCase(AppgridHelper.appGridMetadata.get("gift_products_def").asObject().get("livetv_single_prod_id").asString())
                                && (userProducts.get(i).getBundleFlag()
					|| userProducts.get(i).getProductId().equalsIgnoreCase(
                                        AppgridHelper.appGridMetadata.get("gift_products_def").asObject()
                                        .get("hooq_postpaid_prod_id").asString()))) {
				JsonObject product = new JsonObject();
				product.add("productId", userProducts.get(i).getProductId());
				product.add("cpId", userProducts.get(i).getCpId());
				product.add("productType", userProducts.get(i).getProductType());
				product.add("state", userProducts.get(i).getState());
				product.add("active", userProducts.get(i).getActive());

				int bsbDays = (int) ((userProducts.get(i).getBsbValidity() - Util.getIST()) / 86400000);
				int cpDays = (int) ((userProducts.get(i).getContentValidity() - Util.getIST()) / 86400000);
				int noOfDaysLeft = 0;

				if (bsbDays < cpDays
						|| userProducts
								.get(i)
								.getProductId()
								.equals(AppgridHelper.appGridMetadata.get("gift_products_def").asObject()
										.get("hooq_prepaid_prod_id").asString())) {
					product.add("validity", userProducts.get(i).getBsbValidity());
					noOfDaysLeft = bsbDays;

				} else {
					if (product.get("active").asBoolean()) {
						product.add("validity", userProducts.get(i).getContentValidity());
						noOfDaysLeft = (cpDays == 30 ? 29 : cpDays);
					} else {
						product.add("validity", userProducts.get(i).getContentValidity());
						noOfDaysLeft = 29;
					}
				}

				noOfDaysLeft = noOfDaysLeft + 1;

				product.add("noOfDaysLeft", noOfDaysLeft);

				BundleCounterVO bundleCounter = bundleCounterDao.getBundleCounterByUserWithProductId(userProducts
						.get(i).getProductId(), userId);

				if (bundleCounter != null && !userProducts
						.get(i)
						.getProductId()
						.equalsIgnoreCase(
								AppgridHelper.appGridMetadata.get("gift_products_def").asObject()
										.get("hooq_postpaid_prod_id").asString())) {
					product.add("itemLimit", (bundleCounter.getItemLimit() == 1) ? 0 : bundleCounter.getItemLimit());
					product.add("counter", bundleCounter.getCounter());
				} else {
					product.add("itemLimit", 0);
					product.add("counter", 0);
				}
                                if (SubscriptionHelper.allProductsMap.get(userProducts.get(i).getProductId()) != null) {
                                    product.add("productName", SubscriptionHelper.allProductsMap.get(userProducts.get(i).getProductId())
						.get("title"));
                                } else {
                                    product.add("productName", "");
                                }
				if (AppgridHelper.appGridMetadata.get("gift_products_def").asObject()
						.get(product.get("productId").asString()) != null
						&& !AppgridHelper.appGridMetadata.get("gift_products_def").asObject()
								.get(product.get("productId").asString()).isNull()) {
					JsonObject jsonValue = JsonObject.readFrom(AppgridHelper.appGridMetadata.get("gift_products_def")
							.asObject().get(product.get("productId").asString()).asString());
					product.add("description", jsonValue.get("description"));
					product.add("imageUrl", jsonValue.get("imageUrl"));
				}
				productsList.add(product);
			}
		}
		

		if (!trialFlag && (userProfileDao.getUserProfileByUserId(userId) == null || !userProfileDao.getHooqTrialFlag(userId))) {

			String userType = "";
//			UserDerivedProfile existingUserDerivedProfile = mongoDBUserDerivedProfileDAO
//					.readUserDerivedProfileByUserId(userId);
			UserDerivedProfile existingUserDerivedProfile = null;
			if (existingUserDerivedProfile == null) {
				String bsbProfile = SubscriptionHelper.getUserProfile(userId, headers);
				JsonObject profileObject = JsonObject.readFrom(bsbProfile);
				userType = (profileObject.get("userType") != null && !profileObject.get("userType").isNull()) ? profileObject
						.get("userType").asString() : "";
			}
//			} else {
//				userType = existingUserDerivedProfile.getUserType();
//			}

			JsonObject product = null;

			if (userType.equalsIgnoreCase("postpaid")) {
				product = JsonObject.readFrom(AppgridHelper.appGridMetadata.get("gift_products_def").asObject()
						.get("hooq_postpaid").asString());
				product.add("validity", Util.getIST() + 2592000000.00);
			} else {
				product = JsonObject.readFrom(AppgridHelper.appGridMetadata.get("gift_products_def").asObject()
						.get("hooq_prepaid").asString());
				product.add("validity", Util.getIST() + 1209600.00);
			}

			productsList.add(product);
		}
                return productsList.toString();
	}

	@Override
    public String getRails(String userId, String bsbToken, Boolean airtel, String bsbResponse, String deviceId, boolean showOffer) {
		log.info("Rails call for uid:" + userId);
		String result = null;
		try {
			String newReorderKey = null;
			JsonObject personalizedRail = null;
			JsonObject jsonObject = new JsonObject();
			JsonArray railsJsonArray = new JsonArray();
			JsonArray productsJsonArray = new JsonArray();
			JsonArray railsArray = new JsonArray();
			JsonArray airtelProds = JsonArray.readFrom(AppgridHelper.appGridMetadata.get("gift_products_def")
					.asObject().get("all_airtel_prods").asString());

			if (!bsbToken.equalsIgnoreCase("") && !bsbToken.contains("null")) {
				if (bsbResponse.equalsIgnoreCase("")) {
					bsbResponse = SubscriptionHelper.checkPackStatus(userId, bsbToken, headers);
				}
				JsonObject statusObject = JsonObject.readFrom(bsbResponse);
				Iterator<Member> iterator = statusObject.iterator();
				List<Integer> prd = new ArrayList<Integer>();

				while (iterator.hasNext()) {
					Member key = iterator.next();
					JsonObject bsbProductObject = (statusObject.get(key.getName()) != null && !statusObject.get(
							key.getName()).isNull()) ? statusObject.get(key.getName()).asObject() : null;
					if (bsbProductObject != null && !bsbProductObject.isNull()) {
						JsonObject subscriptionStatus = new JsonObject();
						subscriptionStatus = SubscriptionHelper.evaluateSubscriptionStatus(bsbProductObject,
								key.getName());
						if (subscriptionStatus.get("status").asBoolean()
								&& !subscriptionStatus.get("body").asObject().get("status").asString()
										.equalsIgnoreCase("suspended")) {
							productsJsonArray.add(key.getName());
							prd.add(Integer.parseInt(key.getName()));
						}
					}
				}

				int[] intArray = ArrayUtils.toPrimitive(prd.toArray(new Integer[0]));
				Arrays.sort(intArray);

				String productKey = "";

				for (int i = 0; i < intArray.length; i++) {
					if (intArray[i] != 12005) {
						productKey = productKey + "_" + intArray[i];
					}
				}

				productKey = productKey.replaceFirst("_", "");

				JsonValue railsArr = AppgridHelper.appGridRailConfiguration.get(productKey);

				if (railsArr != null) {
					railsArray = JsonArray.readFrom(AppgridHelper.appGridRailConfiguration.get(productKey).asString());
				}

				if (!airtel) {
					for (int k = 0; k < airtelProds.size(); k++) {
						if (productKey.equalsIgnoreCase(airtelProds.get(k).asString())) {
							railsArray = new JsonArray();
						}
					}
				}

			} else {
				for (int i = 0; i < 10; i++) {
					railsJsonArray.add("rails_" + i);
					productsJsonArray.add("products_" + i);
				}
			}

			/* Get Personalized Rail IDs */
			try {
				personalizedRail = getPersonalizedRails(userId);
				JsonArray personalizedRailsArray = (JsonArray) personalizedRail.get("railsIdsArray");

				/* Add Personalized Rail IDs to railIDs Array */
				for (int k = 0; k < personalizedRailsArray.size(); k++) {
					railsArray.add(personalizedRailsArray.get(k).asString());
				}
			} catch (Exception e) {
				log.error("Personalized Rails Error:" + e);
			}

			jsonObject.add("products", productsJsonArray);

			JsonArray productsArray = new JsonArray();

			for (int i = 0; i < productsJsonArray.size(); i++) {
				String productId = productsJsonArray.get(i).asString();
				if (SubscriptionHelper.allProductsMap.get(productId) != null
						&& !SubscriptionHelper.allProductsMap.get(productId).isEmpty()) {
					JsonObject prodObj = SubscriptionHelper.allProductsMap.get(productId).asObject();
					productsArray.add(prodObj);
				}
			}

			Boolean giftFlag = false;
			String giftProducts = AppgridHelper.appGridMetadata.get("gift_products_def").asObject()
					.get("all_gift_products").asString();

			for (int i = 0; i < productsArray.size(); i++) {
				if (giftProducts.contains(productsArray.get(i).asObject().get("id").asString())) {
					giftFlag = true;
				}
			}

			if (userProfileDao.getUserProfileByUserId(userId) == null || !userProfileDao.getHooqTrialFlag(userId)) {
				giftFlag = true;
				JsonObject prodObj = SubscriptionHelper.allProductsMap.get(
						AppgridHelper.appGridMetadata.get("gift_products_def").asObject().get("hooq_prepaid_prod_id")
								.asString()).asObject();
				productsArray.add(prodObj);
			}

			jsonObject.add("rails", railsArray);
			jsonObject.add("messageKeys", getMsgKeys(productsJsonArray, userId, deviceId, airtel, showOffer));
			jsonObject.add("productsArray", productsArray);
			JsonValue reorderCp = AppgridHelper.appGridMetadata.get("cp_reordering").asObject()
					.get(personalizedRail.get("cp_reorder").asString().toLowerCase());
			jsonObject.add("reordercp", reorderCp != null && !reorderCp.isNull() ? reorderCp.asString() : "");
			jsonObject.add("gift", giftFlag);

			result = jsonObject.toString();
		} catch (BusinessApplicationException e) {
			log.error("User Not Found-, line 149 - " + userId + ":", e);
			throw new BusinessApplicationException(HttpStatus.NOT_FOUND.value(), e, "User Not Found!");
		} catch (Exception e) {
			log.error("Rails Error:" + e);
			throw new BusinessApplicationException(HttpStatus.NOT_FOUND.value(), "Some error occured!");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private JsonObject getPersonalizedRails(String uid) {
		JsonObject cpReorder = new JsonObject();
		String userType = "";
		JsonArray railsIdsArray = new JsonArray();
		List<String> listLangauge = new ArrayList<>();
		List<String> langRailIds = new ArrayList<>();
		List<String> usrTypeRailIds = new ArrayList<>();
		HashMap<String, Object> railMap = new HashMap<String, Object>();
		User existingUserProfile = null;
		// Get User/User Derived Profiles from Mongo DB
		UserDerivedProfile existingUserDerivedProfile = null;

		// Get All Personalized Rails Configurations From Appgrid
		JsonObject circleObject = AppgridHelper.appGridMetadata.get("language_circle").asObject();
		JsonObject languageRailObject = AppgridHelper.appGridMetadata.get("language_rails").asObject();
		JsonObject userTypeRailObject = AppgridHelper.appGridMetadata.get("usertype_rails").asObject();

		// Check if profile exists in Mongo DB and if YES, retrieve railIds from
		// SQl table
		// if (existingUserDerivedProfile != null
		// && (existingUserDerivedProfile.getLanguage() != null &&
		// existingUserDerivedProfile.getLanguage().length > 0)
		// && (existingUserDerivedProfile.getUserType() != null &&
		// !existingUserDerivedProfile.getUserType()
		// .isEmpty())) {
		// userType = existingUserDerivedProfile.getUserType();

		PersonalizedRailVO personalizedRailVOLanguage = personalizedRailDao.getPersonalizedRailByUserIdWithRailType(
				uid, "language");
		PersonalizedRailVO personalizedRailVOUserType = personalizedRailDao.getPersonalizedRailByUserIdWithRailType(
				uid, "usertype");

		railsIdsArray = addtoRailsArray(personalizedRailVOLanguage, railsIdsArray);
		railsIdsArray = addtoRailsArray(personalizedRailVOUserType, railsIdsArray);

		// if (!(railsIdsArray.size() > 0)) {
		// // Map Appgrid config to Rail Ids based on the current user
		// // properties
		// existingUserDerivedProfile =
		// mongoDBUserDerivedProfileDAO.readUserDerivedProfileByUserId(uid);
		// if (existingUserDerivedProfile != null
		// && (existingUserDerivedProfile.getLanguage() != null
		// && existingUserDerivedProfile.getLanguage().length > 0)
		// && (existingUserDerivedProfile.getUserType() != null
		// && !existingUserDerivedProfile.getUserType().isEmpty())) {
		// userType = existingUserDerivedProfile.getUserType();
		// existingUserProfile = mongoDBUserDAO.readUserByUserId(uid);
		// railMap = mapUserPropertiesToRails(circleObject, languageRailObject,
		// userTypeRailObject,
		// existingUserProfile.getCircle(), userType);
		// railsIdsArray = (JsonArray) railMap.get("railids");
		// langRailIds = (List<String>) railMap.get("language");
		// usrTypeRailIds = (List<String>) railMap.get("usertype");
		// }
		// } else {
		//
		// existingUserDerivedProfile =
		// mongoDBUserDerivedProfileDAO.readUserDerivedProfileByUserId(uid);
		// }

		// } else {

		// If NO, call getProfile API
		String bsbProfile = SubscriptionHelper.getUserProfile(uid, headers);

		// Get circle and userType
		JsonObject profileObject = JsonObject.readFrom(bsbProfile);
		String userCircle = (profileObject.get("circle") != null && !profileObject.get("circle").isNull()) ? profileObject
				.get("circle").asString() : "";
		userType = (profileObject.get("userType") != null && !profileObject.get("userType").isNull()) ? profileObject
				.get("userType").asString() : "";

		// Map Appgrid config to Rail Ids based on the current user
		// properties
		railMap = mapUserPropertiesToRails(circleObject, languageRailObject, userTypeRailObject, userCircle, userType);
		railsIdsArray = JsonArray.readFrom(railMap.get("railsids").toString());
		langRailIds = (List<String>) railMap.get("language");
		usrTypeRailIds = (List<String>) railMap.get("usertype");
		// existingUserProfile = mongoDBUserDAO.readUserByUserId(uid);

		// Store user profile in mongo table
		User user = extractUserFromJson(bsbProfile, uid);
		cpReorder.add("railsIdsArray", railsIdsArray);
		cpReorder
				.add("cp_reorder",
						(userCircle
								+ "_"
								+ (user.getOperator().toLowerCase().equals("airtel") ? user.getOperator()
										: "non-airtel") + "_" + (userType != null && !userType.isEmpty() ? userType
								: "unknown")).replaceAll("\\s+", ""));
		// if (existingUserProfile != null) {
		// user.setId(existingUserProfile.getId());
		// mongoDBUserDAO.updateUser(user);
		// } else {
		// user = mongoDBUserDAO.createUser(user);
		// }
		UserDerivedProfile userDerivedProfile = null;
		// Store languages and userType in derived Mongo table
		// UserDerivedProfile userDerivedProfile =
		// mongoDBUserDerivedProfileDAO.readUserDerivedProfileByUserId(uid);
		// if (existingUserDerivedProfile == null) {
		// userDerivedProfile = new UserDerivedProfile();
		// userDerivedProfile.setUserId(uid);
		// userDerivedProfile.setUserType(userType);
		// userDerivedProfile.setLanguage(listLangauge.toArray(new
		// String[listLangauge.size()]));
		// mongoDBUserDerivedProfileDAO.createUserDerivedProfile(userDerivedProfile);
		// } else {
		// existingUserDerivedProfile.setUserType(userType);
		// existingUserDerivedProfile.setLanguage(listLangauge.toArray(new
		// String[listLangauge.size()]));
		// mongoDBUserDerivedProfileDAO.updateUserDerivedProfile(existingUserDerivedProfile);
		// }

		// Add rail ids & rail type in mysql table (from appgrid mapping)
		try {
			insertAllRails(uid, langRailIds, usrTypeRailIds);
		} catch (Exception e) {
			log.error("Insert to Rail Ids Table Failed due to: " + e);
		}

		// }
		return cpReorder;
	}
	
	@SuppressWarnings("unchecked")
	private JsonObject getPersonalizedCards(String uid) {
		JsonObject cpReorder = new JsonObject();
		String userType = "";
		JsonArray railsIdsArray = new JsonArray();
		
		List<String> langRailIds = new ArrayList<>();
		List<String> usrTypeRailIds = new ArrayList<>();
		HashMap<String, Object> railMap = new HashMap<String, Object>();

		// Get All Personalized Rails Configurations From Appgrid
		JsonObject circleObject = AppgridHelper.appGridMetadata.get("language_circle").asObject();
		JsonObject languageRailObject = AppgridHelper.appGridMetadata.get("language_rails").asObject();
		JsonObject userTypeRailObject = AppgridHelper.appGridMetadata.get("usertype_rails").asObject();

		PersonalizedRailVO personalizedRailVOLanguage = personalizedRailDao.getPersonalizedRailByUserIdWithRailType(
				uid, "language");
		PersonalizedRailVO personalizedRailVOUserType = personalizedRailDao.getPersonalizedRailByUserIdWithRailType(
				uid, "usertype");

		railsIdsArray = addtoRailsArray(personalizedRailVOLanguage, railsIdsArray);
		railsIdsArray = addtoRailsArray(personalizedRailVOUserType, railsIdsArray);
		
		// If NO, call getProfile API
		String bsbProfile = SubscriptionHelper.getUserProfile(uid, headers);

		// Get circle and userType
		JsonObject profileObject = JsonObject.readFrom(bsbProfile);
		String userCircle = (profileObject.get("circle") != null && !profileObject.get("circle").isNull()) ? profileObject
				.get("circle").asString() : "";
		userType = (profileObject.get("userType") != null && !profileObject.get("userType").isNull()) ? profileObject
				.get("userType").asString() : "";

		// Map Appgrid config to Rail Ids based on the current user
		// properties
		railMap = mapUserPropertiesToRails(circleObject, languageRailObject, userTypeRailObject, userCircle, userType);
		railsIdsArray = JsonArray.readFrom(railMap.get("railsids").toString());
		langRailIds = (List<String>) railMap.get("language");
		usrTypeRailIds = (List<String>) railMap.get("usertype");
		// existingUserProfile = mongoDBUserDAO.readUserByUserId(uid);

		// Store user profile in mongo table
		User user = extractUserFromJson(bsbProfile, uid);
		cpReorder.add("railsIdsArray", railsIdsArray);
		cpReorder
				.add("cp_reorder",
						(userCircle
								+ "_"
								+ (user.getOperator().toLowerCase().equals("airtel") ? user.getOperator()
										: "non-airtel") + "_" + (userType != null && !userType.isEmpty() ? userType
								: "unknown")).replaceAll("\\s+", ""));

		// Add rail ids & rail type in mysql table (from appgrid mapping)
		try {
			insertAllRails(uid, langRailIds, usrTypeRailIds);
		} catch (Exception e) {
			log.error("Insert to Rail Ids Table Failed due to: " + e);
		}

		// }
		return cpReorder;
	}

	private HashMap<String, Object> mapUserPropertiesToRails(JsonObject circleObject, JsonObject languageRailObject,
			JsonObject userTypeRailObject, String userCircle, String userType) {
		Iterator<Member> iterator = circleObject.iterator();
		JsonArray languageJsonArray = new JsonArray();
		List<String> listLangauge = new ArrayList<>();
		List<String> langRailIds = new ArrayList<>();
		List<String> usrTypeRailIds = new ArrayList<>();
		JsonArray railsIdsArray = new JsonArray();
		HashMap<String, Object> response = new HashMap<String, Object>();

		while (iterator.hasNext()) {
			Member key = iterator.next();
			if (circleObject.get(key.getName()) != null && !circleObject.get(key.getName()).isNull()
					&& circleObject.get(key.getName()).asString().contains(userCircle)) {
				languageJsonArray.add(key.getName());
				listLangauge.add(key.getName());
			}
		}

		JsonArray languageRailArray = new JsonArray();

		for (int i = 0; i < languageJsonArray.size(); i++) {
			JsonArray languageArrayTemp = JsonArray.readFrom(languageRailObject
					.get(languageJsonArray.get(i).asString()).asString());
			for (int j = 0; j < languageArrayTemp.size(); j++) {
				languageRailArray.add(languageArrayTemp.get(j));
			}
		}

		JsonArray userTypeRailArray = JsonArray
				.readFrom((userTypeRailObject.get(userType.toLowerCase()) != null && !userTypeRailObject.get(
						userType.toLowerCase()).isNull()) ? userTypeRailObject.get(userType.toLowerCase()).asString()
						: "[]");

		for (int i = 0; i < languageRailArray.size(); i++) {
			railsIdsArray.add(languageRailArray.get(i).asString());
			langRailIds.add(languageRailArray.get(i).asString());
		}
		for (int j = 0; j < userTypeRailArray.size(); j++) {
			
			railsIdsArray.add(userTypeRailArray.get(j).asString());
			usrTypeRailIds.add(userTypeRailArray.get(j).asString());
		}

		response.put("railsids", railsIdsArray);
		response.put("language", langRailIds);
		response.put("usertype", usrTypeRailIds);

		return response;

	}

	private void insertAllRails(String uid, List<String> langRailIds, List<String> usrTypeRailIds) {
		PersonalizedRailVO personalizedLangRails = personalizedRailDao.getPersonalizedRailByUserIdWithRailType(uid,
				"language");
		PersonalizedRailVO personalizedUserTypeRails = personalizedRailDao.getPersonalizedRailByUserIdWithRailType(uid,
				"usertype");

		UserProfile userProfile = new UserProfile();
		userProfile.setUserId(uid);

		saveRailIds(langRailIds, personalizedLangRails, userProfile, "language");
		saveRailIds(usrTypeRailIds, personalizedUserTypeRails, userProfile, "userType");
	}

	private void saveRailIds(List<String> usrTypeRailIds, PersonalizedRailVO personalizedUserTypeRails,
			UserProfile userProfile, String railType) {
		PersonalizedRail personalizedRail;
		personalizedRail = new PersonalizedRail();
		personalizedRail.setUserProfile(userProfile);
		personalizedRail.setRailIds(usrTypeRailIds.toArray(new String[usrTypeRailIds.size()]));
		personalizedRail.setRailType(railType);
		if (personalizedUserTypeRails == null) {
			personalizedRailDao.createPersonalizedRail(personalizedRail);
		} else {
			personalizedRail.setId(Long.parseLong(personalizedUserTypeRails.getId()));
			personalizedRailDao.updatePersonalizedRail(personalizedRail);
		}
	}

	private JsonArray addtoRailsArray(PersonalizedRailVO personalizedRailVO, JsonArray railsIdsArray) {
		if (personalizedRailVO != null) {
			for (int i = 0; i < personalizedRailVO.getRailIds().length; i++) {
				railsIdsArray.add(personalizedRailVO.getRailIds()[i]);
			}
		}

		return railsIdsArray;
	}

	private User extractUserFromJson(String userJson, String uid) {
		User user = new User();
		JsonObject profileJsonObject = JsonObject.readFrom(userJson);
		user.setMsisdnDetected("");
		user.setStatus("");
		user.setUid(uid);
		user.setToken("");
		user.setOperator((profileJsonObject.get("operator") != null && !profileJsonObject.get("operator").isNull()) ? profileJsonObject
				.get("operator").asString() : "");
		user.setCircle((profileJsonObject.get("circle") != null && !profileJsonObject.get("circle").isNull()) ? profileJsonObject
				.get("circle").asString() : "");
		user.setUserType((profileJsonObject.get("userType") != null && !profileJsonObject.get("userType").isNull()) ? profileJsonObject
				.get("userType").asString() : "");
		user.setIcrCircle(profileJsonObject.get("icrCircle").asBoolean() ? "true" : "false");
		user.setEapSim("");
		return user;
	}

	private String getMsgKeys(JsonArray products, String uid, String deviceId, Boolean airtel, boolean showOffer) {

		String productGroup = "[12900,12905,12901,12902,12907,12908]";
		JsonObject messages = AppgridHelper.appGridMetadata.get("messageKeys").asObject();
		String productsMessage = "";
		String messageKey = "";
		String productsList = "";
		String finalMessage = "";

		for (int i = 0; i < products.size(); i++) {
			ProductVO productVo = productDao.getProductByUserWithProductId(products.get(i).asString(), uid);
			if (productGroup.contains(products.get(i).asString()) && checkProductMessageShownState(productVo)) {
				productsList = productsList + "_" + products.get(i).asString();
				if (productVo != null) {
					productDao.updateProductMessageFlag(uid, products.get(i).asString(), true, Util.getIST());
				}
			}
		}

		messageKey = productsList;

		if (!productsList.isEmpty() && messages.get(messageKey) != null && !messages.get(messageKey).isNull()) {
			productsMessage = messages.get(messageKey).asString();
		}

		String offerString = "";
		if(showOffer){
			offerString = getOffers(uid, deviceId);
		}
		if (!offerString.isEmpty()) {
			messageKey = productsList + offerString;
			finalMessage = messages.get(messageKey).asString();
		} else {
			finalMessage = productsMessage;
		}

		// log.info("final message for uid:" + uid + "msg:" + finalMessage);
		return finalMessage;
	}

	private Boolean checkProductMessageShownState(ProductVO productVO) {
		if (productVO == null
				|| (!productVO.getActive() && (!productVO.isMessageFlag() || (Util.getIST() - productVO
						.getMessageTimeStamp()) > 604800000))) {
			return true;
		} else {
			return false;
		}
	}

	private String getOffers(String uid, String deviceId) {
		String response = "";
		try {
			String offerIds = AppgridHelper.appGridMetadata.get("offer_ids").asString();
			String offer_Ids = "[9001]";
			JsonArray offerIdsArray = JsonArray.readFrom(offerIds);
			OfferVO offerVo = offerDao.getOfferByUserIdOfferId(uid, "9001");

			if (offerVo == null || offerVo.getOfferValidity() < Util.getIST()) {
				String offerResponseBSB = SubscriptionHelper.getOfferProvision(uid, offer_Ids, deviceId, "", "", headers);
				JsonObject offerResponse = JsonObject.readFrom(offerResponseBSB);
				JsonObject moengagePayload = new JsonObject();
				JsonArray actionsArray = new JsonArray();
				JsonObject actionsObject = new JsonObject();
				JsonObject attributesObject = new JsonObject();

				for (JsonValue offerId : offerIdsArray) {
					JsonObject offer = offerResponse.get(offerId.asString()).asObject();

					if (offer.get("status").asString().equalsIgnoreCase("eligible")
							|| offer.get("status").asString().equalsIgnoreCase("availed")) {
						OfferVO offerVO = offerDao.getOfferByUserIdOfferId(uid, "9001");
						if (offerVO != null && offerVO.getOfferValidity() < offer.getLong("validTillTimestamp", 0)) {
							offerDao.updateOfferValidity(offerId.asString(), uid,
									offer.getLong("validTillTimestamp", 0));
							moengagePayload.add("type", "event");
							moengagePayload.add("customer_id", uid);
							moengagePayload.add("device_id", deviceId);
							attributesObject.add("offerId", offerId.asString());
							attributesObject.add("offerValidity", offer.getLong("validTillTimestamp", 0));
							attributesObject.add("offerName", "Data Offer");
							actionsObject.add("action", "offerNotification");
							actionsObject.add("attributes", attributesObject);
							actionsObject.add("platform", "WEB");
							actionsArray.add(actionsObject);
							moengagePayload.add("actions", actionsArray);
							SubscriptionHelper.updateMongageEvent(headers, moengagePayload.toString());
							response = response + "_" + offerId.asString();
						} else if (offerVO == null) {
							Offer newoffer = new Offer();
							UserProfileVO user = userProfileDao.getUserProfileByUserId(uid);
							if (user != null) {
								UserProfile userProfile = new UserProfile();
								userProfile.setUserId(uid);
								newoffer.setOfferId(offerId.asString());
								newoffer.setOfferValidity(offer.getLong("validTillTimestamp", 0));
								newoffer.setOfferShownFlag(true);
								newoffer.setofferStatus(offer.get("status").asString());
								newoffer.setUserProfile(userProfile);
								offerDao.createOffer(newoffer);
							}
							moengagePayload.add("type", "event");
							moengagePayload.add("customer_id", uid);
							moengagePayload.add("device_id", deviceId);
							attributesObject.add("offerId", offerId.asString());
							attributesObject.add("offerValidity", offer.getLong("validTillTimestamp", 0));
							attributesObject.add("offerName", "Data Offer");
							actionsObject.add("action", "offerNotification");
							actionsObject.add("attributes", attributesObject);
							actionsObject.add("platform", "WEB");
							actionsArray.add(actionsObject);
							moengagePayload.add("actions", actionsArray);
							SubscriptionHelper.updateMongageEvent(headers, moengagePayload.toString());
							response = response + "_" + offerId.asString();
						}
					}
				}
			}
		} catch (Exception e) {
			// log.error("Error in Get Offers");
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return response;
	}

	/* Get Recent-list from Appgrid */
	@Override
	public String getRecentList(final String userId, final String dataKey, String cpIds) {
		String response = "";
		String mpxResponse = null;
		String assetIds = null;
		String movieAssetIds = null;
		List<RecentVO> recentVOs = null;
		try {
			recentVOs = recentDao.getRecentListUserId(userId);
			if (recentVOs != null && recentVOs.size() > 0) {
				assetIds = getMpxFeedAssetIds(recentVOs);
				movieAssetIds = assetIds;
				movieAssetIds = movieAssetIds.replace("|", "~");
			}
			response = null;

			if ((cpIds == null || cpIds.isEmpty()) || (cpIds.contains(","))) {
				if (assetIds != null && !assetIds.isEmpty()) {
					mpxResponse = getFavouritesFeedMpx(assetIds, cpIds);
					String creditsByProgramIdsUrl = AppgridHelper.mpxFeedData.get("creditsbyprogramid").asString();
					mpxResponse = ContentProviderUtil.getProgramWithAvailableCredits(mpxResponse,
							creditsByProgramIdsUrl, headers);
					response = ContentProviderHelper.createJsonResponse(mpxResponse);
				}
			}
			response = appendLastWatchedDetails(recentVOs, response, movieAssetIds);
			if (response == null || response.isEmpty()) {
				response = "{\"entries\":[]}";
			}
		} catch (HttpClientErrorException e) {
			// log.error("Error in Removing fav- " + userId + ":", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (Exception e) {
			// log.error("Error in Removing fav- " + userId + ":", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return response;
	}

	/* Get Favorites from Appgrid */
	@Override
	public String getFavourites(final String userId, final String dataKey, String cpIds) {
		String response = "";
		String assetIds = "";
		List<FavouriteVO> favouriteVOs = null;
		try {
			favouriteVOs = favouriteDao.getFavouriteListByUserId(userId);
			if (favouriteVOs != null && favouriteVOs.size() > 0) {
				assetIds = getMpxFeedAssetIdsFromFavouriteList(favouriteVOs);
			}
			response = null;
			if ((cpIds == null || cpIds.isEmpty()) || (cpIds.contains(","))) {
				if (assetIds != null && !assetIds.isEmpty()) {
					response = getFavouritesFeedMpx(assetIds, cpIds);
					String creditsByProgramIdsUrl = AppgridHelper.mpxFeedData.get("creditsbyprogramid").asString();
					response = ContentProviderUtil.getProgramWithAvailableCredits(response, creditsByProgramIdsUrl,
							headers);
					response = ContentProviderHelper.createJsonResponse(response);
				}

			}
			if (response == null || response.isEmpty()) {
				response = "{\"entries\":[]}";
			}
		} catch (HttpClientErrorException e) {
			log.error("Error in external API call- " + userId + ":", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (Exception e) {
			log.error("Error in Get Favourites- " + userId + ":", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return response;
	}

	/* Get MSP window */
	@Override
	public String getMsp() {
		return appGridMetadata.get("msp_window").asString();
	}

	@Override
	public String getUserTokenFromMPX(String uid, String contextPath, String token, String bsbResponse) {
		String response = "";
		String productId = "";

		List<String> productArray = new ArrayList<String>();
		try {
			if (AppgridHelper.appGridMetadata.get("bsb_flag").toString().equalsIgnoreCase("\"true\"")) {
				String bsbToken = JsonObject.readFrom(token).get("token").asString();
				if (bsbResponse.equalsIgnoreCase("")) {
					bsbResponse = SubscriptionHelper.checkPackStatus(uid, bsbToken, headers);
				}
				JsonObject statusObject = JsonObject.readFrom(bsbResponse);

				HashMap<String, Long> cpValidityMap = new HashMap<String, Long>();

				for (int i = 0; i < SubscriptionHelper.allProductsArray.size(); i++) {
					productId = SubscriptionHelper.allProductsArray.get(i).asObject().get("id").asString();
					JsonObject bsbProductObject = (statusObject.get(productId) != null && !statusObject.get(productId)
							.isNull()) ? statusObject.get(productId).asObject() : null;
					if (bsbProductObject != null && !bsbProductObject.isNull()) {
						JsonObject subscriptionStatus = new JsonObject();
						subscriptionStatus = SubscriptionHelper.evaluateSubscriptionStatus(bsbProductObject, productId);
						String cpId = SubscriptionHelper.allProductsArray.get(i).asObject().get("contentProvider")
								.asString().toUpperCase();
						if (subscriptionStatus.get("status").asBoolean()) {
							ProductVO productVO = productDao.getProductByUserWithProductId(productId, uid);
							long bsbValidity = bsbProductObject.get("expireTimestamp").asLong();
							String bsbStatus = bsbProductObject.get("status").asString();
							
							if(productVO != null && (productVO.getBsbValidity() != bsbValidity))
							{
								productDao.updateProductBsbValidity(uid, productId, bsbValidity);
							}
							
							if (productVO == null
									|| (productVO.getContentValidity() <= System.currentTimeMillis() && productVO
											.getContentValidity() != 0)) {

								long monthInMs = 0;
								long contentValidity = System.currentTimeMillis() + monthInMs;
								int productCycle = SubscriptionHelper.allProductsArray.get(i).asObject()
										.get("productCycle").asInt();

								if (productCycle == 1) {
									monthInMs = (long) 86400000.00;
								} else if (productCycle == 7) {
									monthInMs = (long) 604800000.00;
								} else {
									monthInMs = (long) 2592000000.00;
								}

								contentValidity = System.currentTimeMillis() + monthInMs;
								String cp = SubscriptionHelper.allProductsArray.get(i).asObject()
										.get("contentProvider").asString().toUpperCase();

								contentValidity = (cpValidityMap.get(cp) != null && cpValidityMap.get(cp) != 0) ? cpValidityMap
										.get(cp) : contentValidity;

								List<ProductVO> currentLiveProduct = productDao.getProductsByUserId(uid);
								for (int j = 0; currentLiveProduct.size() > j; j++) {
									ProductVO prod = currentLiveProduct.get(j);
									if (prod.getCpId().equalsIgnoreCase(cp) && prod.getLive() && !prod.getBundleFlag()) {
										contentValidity = prod.getContentValidity() > System.currentTimeMillis() ? prod
												.getContentValidity() : 0;
									}
								}

								boolean bundleFlag = (SubscriptionHelper.allProductsMap.get(productId).asObject()
										.get("bundleFlag") != null && !SubscriptionHelper.allProductsMap.get(productId)
										.asObject().get("bundleFlag").isNull()) ? SubscriptionHelper.allProductsMap
										.get(productId).asObject().get("bundleFlag").asBoolean() : false;
								String productType = SubscriptionHelper.allProductsMap.get(productId).asObject()
										.get("productType").asString();

                                if(!bundleFlag || isSingleVideoProduct(productId)) {
									contentValidity = bsbValidity;
								}

								if (bsbStatus.equalsIgnoreCase("suspended") && productVO != null) {
									contentValidity = productVO.getContentValidity();
								}

								addProduct(uid, productId, cp, productType, bundleFlag, bsbValidity, contentValidity,
										headers, subscriptionStatus.get("body").asObject());

								if (productId.equalsIgnoreCase(AppgridHelper.appGridMetadata.get("gift_products_def")
										.asObject().get("hooq_postpaid_prod_id").asString())) {
									userProfileDao.setHooqTrialFlag(uid);
								}

								if (productVO != null && productVO.getContentValidity() <= System.currentTimeMillis()) {
									productDao.updateProductRenewal(uid, productId);
								}

								if (!bundleFlag
										&& (!cp.equalsIgnoreCase("SINGTEL") || bsbStatus.equalsIgnoreCase("suspended"))) {
									productDao.updateProductActive(uid, productId, true);
									productDao.updateProductActivated(uid, productId);
								}

							}

							BundleCounterVO bundleCounterVO = bundleCounterDao.getBundleCounterByUserWithProductId(
									productId, uid);
							if (SubscriptionHelper.bundleCheck(productId) && bundleCounterVO == null) {
								createBundleInProfile(uid, productId, cpId, getBundleLimit(productId, cpId));
							}

							if (SubscriptionHelper.allProductsArray.get(i).asObject().get("contentProvider").asString()
									.toUpperCase().equalsIgnoreCase("SINGTEL")
									&& (!SubscriptionHelper.bundleCheck(productId) || productId
											.equals(AppgridHelper.appGridMetadata.get("gift_products_def").asObject()
													.get("hooq_prepaid_prod_id").asString()))
									&& !bsbStatus.equalsIgnoreCase("suspended")
									&& !productDao.getProductByUserWithProductId(productId, uid).getActive()) {
								UserProfileVO userProfileVOSingtel = userProfileDao.getUserLoginDetails(uid);
								response = SubscriptionHelper.createSingtelUser(uid, productId,
										userProfileVOSingtel.getEmail(), headers);
								JsonObject reponseJsonObject = JsonObject.readFrom(response);
								if (reponseJsonObject.get("status").asInt() == 1) {
									productDao.updateProductActive(uid, productId, true);
									productDao.updateProductActivated(uid, productId);
									log.info("HOOQ Product Activated in DB in usi for uid:" + uid + ", product:"
											+ productId);
								}
								setSingtelSubscription(uid, productId, statusObject);
							}
							productArray.add(productId);
						} else {
							deleteProduct(uid, productId);
							productDao.updateProductLiv(uid, cpId, true);
						}
					} else {
						ProductVO product = productDao.getProductByUserWithProductId(productId, uid);
						if (product != null) {
							productDao.updateProductLiv(uid, product.getCpId(), true);
							if (product.getLive() && !product.getBundleFlag()) {
								long cpVal = product.getContentValidity() > System.currentTimeMillis() ? product
										.getContentValidity() : 0;
								cpValidityMap.put(product.getCpId(), cpVal);
							}
						}
						deleteProduct(uid, productId);
					}
				}
			} else {
				List<String> productIds = productDao.getProductIdsByUserId(uid);
				for (String product : productIds) {
					productArray.add(product);
				}
			}

			JsonObject mpxAttributeObject = new JsonObject();
			if (productArray.size() > 0) {
				for (int i = 0; i < productArray.size(); i++) {
					mpxAttributeObject.add("subscription", "product_id_" + productArray.get(i).toString());
				}
			}
			// Create new MPX token
			response = new TrustedAuth(contextPath).getToken(uid, mpxAttributeObject.toString());
		} catch (BusinessApplicationException e) {
			log.error("Error , line 531- " + uid + ":", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		} catch (Exception e) {
			log.error("Error , line 535- " + uid + ":", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return response;
	}

    private boolean isSingleVideoProduct(String productId) {
        if(AppgridHelper.appGridMetadata.get("gift_products_def").asObject().get("singleVideoPacksList") != null) {
            String[] ids = AppgridHelper.appGridMetadata.get("gift_products_def").asObject().get("singleVideoPacksList")
                    .asString().split(",");
            for(String id : ids)
                if(id.equalsIgnoreCase(productId))
                    return true;
        }
        return false;
    }

    private String getMpxFeedAssetIds(List<RecentVO> recentVOs) {
		StringBuffer assetIds = new StringBuffer();
		for (RecentVO recentVO : recentVOs) {
			String assetId = recentVO.getAssetId();
			if (assetId != null && !assetId.isEmpty()) {
				char pipe = '|';
				assetIds.append(assetId).append(pipe);
			}
		}
		return assetIds.toString();
	}

	private String getMpxFeedAssetIdsFromFavouriteList(List<FavouriteVO> favouriteVOs) {
		StringBuffer assetIds = new StringBuffer();
		for (FavouriteVO favouriteVO : favouriteVOs) {
			String assetId = favouriteVO.getAssetId();
			if (assetId != null && !assetId.isEmpty()) {
				char pipe = '|';
				assetIds.append(assetId).append(pipe);
			}
		}
		return assetIds.toString();
	}

	/* Support method for getting Favorite list from MPX */
	private String getFavouritesFeedMpx(String assetIds, String cpIds) {
		String response = "";
		String baseURL = AppgridHelper.mpxFeedData.get("programbyid").asString();
		try {
			cpIds = cpIds.replace(",", "|");
			baseURL = baseURL.replace("{0}", assetIds);
			baseURL = baseURL + "&byTags=" + cpIds;
			response = Util.executeApiGetCall(baseURL);
		} catch (HttpClientErrorException e) {
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		}
		return response;
	}

	private String appendLastWatchedDetails(List<RecentVO> recentVOs, String mpxResponse, String movieAssetIds) {
		String response = "";
		JsonObject mpxJsonObject;
		RecentVO recentVO = null;
		JsonObject mpxResponseObject = mpxResponse != null ? JsonObject.readFrom(mpxResponse).asObject() : null;
		JsonArray mpxResponseJsonArray = mpxResponseObject != null ? (JsonArray) mpxResponseObject.get(messageSource
				.getMessage(CPConstants.WYNKSTUDIO_JSON_FIELD_ENTRIES, null, "", Locale.ENGLISH)) : new JsonArray();
		JsonArray entriesJsonArray = new JsonArray();
		String[] apgridAssetIds = movieAssetIds != null ? !movieAssetIds.isEmpty() ? movieAssetIds.split("~") : null
				: null;
		int i = 0;
		if (apgridAssetIds != null) {
			for (int t = apgridAssetIds.length - 1; t >= 0; t--) {
				for (i = 0; i < mpxResponseJsonArray.size(); i++) {
					mpxJsonObject = mpxResponseJsonArray.get(i).asObject();
					recentVO = recentVOs.get(t);
					if (mpxJsonObject.get(messageSource.getMessage(CPConstants.WYNKSTUDIO_JSON_KEY_ID, null, "",
							Locale.ENGLISH)) != null
							&& mpxJsonObject
									.get(messageSource.getMessage(CPConstants.WYNKSTUDIO_JSON_KEY_ID, null, "",
											Locale.ENGLISH)).asString().equals(apgridAssetIds[t])) {
						entriesJsonArray = putLatestWatchedDetails(mpxJsonObject, recentVO, entriesJsonArray);
						mpxResponseJsonArray.remove(i);
						break;
					}
				}
			}
		}
		mpxResponseObject = mpxResponseObject != null ? mpxResponseObject : new JsonObject();
		mpxResponseObject.set(
				messageSource.getMessage(CPConstants.WYNKSTUDIO_JSON_FIELD_ENTRIES, null, "", Locale.ENGLISH),
				entriesJsonArray);
		response = mpxResponseObject.toString();
		return response;
	}

	private JsonArray putLatestWatchedDetails(JsonObject mpxJsonObject, RecentVO recentVO, JsonArray entriesJsonArray) {
		mpxJsonObject
				.add(messageSource.getMessage(CPConstants.WYNKSTUDIO_JSON_KEY_LASTWATCHEDPOSITION, null, "",
						Locale.ENGLISH), recentVO.getLastWatchedPosition());
		mpxJsonObject.add(
				messageSource.getMessage(CPConstants.WYNKSTUDIO_JSON_KEY_LASTWATCHEDTIME, null, "", Locale.ENGLISH),
				recentVO.getLastWatchedTime());
		if (recentVO.getDuration() != null) {
			mpxJsonObject.add(
					messageSource.getMessage(CPConstants.WYNKSTUDIO_JSON_KEY_DURATION, null, "", Locale.ENGLISH),
					recentVO.getDuration());
		} else {
			mpxJsonObject.add(
					messageSource.getMessage(CPConstants.WYNKSTUDIO_JSON_KEY_DURATION, null, "", Locale.ENGLISH), 0);
		}

		entriesJsonArray.add(mpxJsonObject);
		return entriesJsonArray;
	}

	private String getSeasonseBaseUrlByCpId() {
		String baseUrl = "";
		try {
			if (AppgridHelper.mpxFeedData != null) {
				baseUrl = AppgridHelper.mpxFeedData != null ? AppgridHelper.mpxFeedData.get("seasonlistbyseriesid")
						.asString() : null;
			}
		} catch (Exception e) {
			log.error("Season Base URL retrieval failed - ", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e, e.getMessage());
		}
		return baseUrl;
	}

	private void createBundleInProfile(String uid, String productId, String cpId, int limit) {
		try {
			BundleCounterVO bundleCounterVO = bundleCounterDao.getBundleCounterByUserWithProductId(productId, uid);
			if (bundleCounterVO == null) {
				BundleCounter bundleCounter = new BundleCounter();
				bundleCounter.setCpId(cpId);
				bundleCounter.setItemLimit(limit);
				bundleCounter.setProductId(productId);
				UserProfile userProfile = new UserProfile();
				userProfile.setUserId(uid);
				bundleCounter.setUserProfile(userProfile);
				bundleCounter.setCounter(0);
				bundleCounterDao.createBundleCounter(bundleCounter);
			}
		} catch (HttpClientErrorException e) {
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (Exception e) {
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		}
		return;
	}

	private int getBundleLimit(String productId, String cpToken) {
		JsonArray cpProducts = SubscriptionHelper.productsList.get(cpToken.toUpperCase());
		int limit = 0;
		for (JsonValue cpProduct : cpProducts) {
			if (cpProduct.asObject().get("id").asString().equalsIgnoreCase(productId)) {
                            if (cpProduct.asObject().get("bundleLimit") != null)
				limit = cpProduct.asObject().get("bundleLimit").asInt();
                            else
                                limit = 0;
			}
		}
		return limit;
	}

	private void addProduct(String userId, String productId, String cpId, String productType, Boolean bundleFlag,
			Long bsbValidity, Long contentValidity, HttpHeaders headers, JsonObject statusObject) {
		deleteProduct(userId, productId);
		productDao.updateProductLiv(userId, cpId, false);
		Product product = ProducttHelper.toProductWithDiffernetFields(userId, productId, cpId, productType, bundleFlag,
				bsbValidity, contentValidity, statusObject);
		productDao.createProduct(product);

	}

	private void deleteProduct(String userId, String productId) {
		Product product;
		ProductVO productVO = productDao.getProductByUserWithProductId(productId, userId);
		if (productVO != null) {
			product = new Product();
			product.setId(Long.parseLong(productVO.getId()));
			productDao.deleteProduct(product);
		}
		BundleCounterVO bundleCounterVO = bundleCounterDao.getBundleCounterByUserWithProductId(productId, userId);
		if (bundleCounterVO != null) {
			bundleCounterDao.deleteBundleCounter(bundleCounterVO.getId());
		}
	}

	private Boolean setSingtelSubscription(String userId, String SKU, JsonObject statusObject) {
		userProfileDao.updateUserProfileCreatedflag(userId, true);
		return true;
	}

	private String getCpReorder(String uid) {

		String newReorderKey = null;
		String bsbProfile = SubscriptionHelper.getUserProfile(uid, headers);
		JsonObject profileObject = JsonObject.readFrom(bsbProfile);
		// newReorderKey = profileObject.get("circle").asString().trim() + "_"
		// + profileObject.get("operator").asString().trim() + "_"
		// + profileObject.get("userType").asString().trim();
		// newReorderKey = newReorderKey.replaceAll("\\s+", "");
		newReorderKey = ((profileObject.get("circle") != null && !profileObject.get("circle").isNull() ? profileObject
				.get("circle").asString() : "unknown")
				+ "_"
				+ (profileObject.get("operator") != null && !profileObject.get("operator").isNull() ? profileObject
						.get("operator").asString().toLowerCase().equals("airtel") ? profileObject.get("operator")
						.asString().toLowerCase() : "non-airtel" : "non-airtel") + "_" + (profileObject.get("userType") != null
				&& !profileObject.get("userType").isNull() && !profileObject.get("userType").asString().isEmpty() ? profileObject
				.get("userType").asString().toLowerCase()
				: "unknown")).replaceAll("\\s+", "");

		return newReorderKey;

	}
	
	/* Remove recent for a particular key from User Profile in AppGrid */
	@Override
	public String removeRecent(String userId, String assetId) {
		String response = "{\"code\":200,\"body\":\"Successfully removed\"}";
		try {
			recentDao.deleteRecentByUserIdWithAssetId(assetId, userId);
		} catch (Exception e) {
			// log.error("Error in Removing recent- " + userId + ":", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return response;
	}

	@Override
	public String getCards(String userId, String bsbToken, Boolean airtel, String bsbResponse, String deviceId,
			boolean showOffer, String deviceOs, String appVersion) {
		log.info("Cards call for uid:" + userId);
		String result = null;
		try {
			JsonObject personalizedCard = null;
			JsonObject jsonObject = new JsonObject();
			JsonArray railsJsonArray = new JsonArray();
			JsonArray productsJsonArray = new JsonArray();
			JsonArray railsArray = new JsonArray();
			JsonArray cardsArray = new JsonArray();
			JsonArray productIDForCards = new JsonArray();
			JsonArray cardProducts = new JsonArray();
			JsonArray airtelProds = JsonArray.readFrom(AppgridHelper.appGridMetadata.get("gift_products_def")
					.asObject().get("all_airtel_prods").asString());
			JsonArray cardsFromAppgrid = new JsonArray();

			if (!bsbToken.equalsIgnoreCase("") && !bsbToken.contains("null")) {
				if (bsbResponse.equalsIgnoreCase("")) {
					bsbResponse = SubscriptionHelper.checkPackStatus(userId, bsbToken, headers);
				}
				JsonObject statusObject = JsonObject.readFrom(bsbResponse);
				Iterator<Member> iterator = statusObject.iterator();
				List<Integer> prd = new ArrayList<Integer>();

				while (iterator.hasNext()) {
					Member key = iterator.next();
					JsonObject bsbProductObject = (statusObject.get(key.getName()) != null && !statusObject.get(
							key.getName()).isNull()) ? statusObject.get(key.getName()).asObject() : null;
					if (bsbProductObject != null && !bsbProductObject.isNull()) {
						JsonObject subscriptionStatus = new JsonObject();
						subscriptionStatus = SubscriptionHelper.evaluateSubscriptionStatus(bsbProductObject,
								key.getName());
						if (subscriptionStatus.get("status").asBoolean()
								&& !subscriptionStatus.get("body").asObject().get("status").asString()
										.equalsIgnoreCase("Active")) {
							productsJsonArray.add(key.getName());
							prd.add(Integer.parseInt(key.getName()));
						}
					}
				}

				int[] intArray = ArrayUtils.toPrimitive(prd.toArray(new Integer[0]));
				Arrays.sort(intArray);

				
				String productKey = "";
				productKey = Arrays.toString(intArray);
				productKey = productKey.replace("[", "");
				productKey = productKey.replace("]", "");
				productKey = productKey.replace(" ", "");
//				productKey = productKey.replaceFirst("_", "");
						String [] arr = new String[10];
						arr = productKey.split(",");
						for(int i=0;i<arr.length; i++){
							cardProducts.add(arr[i]);
						}
				
//				JsonValue cardArr = AppgridHelper.appGridCardConfiguration.get(productKey);
						

				if (cardProducts != null) {
					for(int i=0;i<cardProducts.size(); i++){
//					railsArray = JsonArray.readFrom(AppgridHelper.appGridRailConfiguration.get(productKey).asString());
						
						boolean activeProductFromDB = productDao.getActiveProductByUserIdAndProductId(userId, cardProducts.get(i).asString());
					if(activeProductFromDB){
//						cardsArray = cardsArray.add(cardProducts.get(i).asString());
//						productIDForCards = productIDForCards.add(cardProducts.get(i).asString());
//						System.out.println(cardProducts.get(i).asString());
//						System.out.println(AppgridHelper.appGridCardConfiguration.get(cardProducts.get(i).asString()));
						if(cardProducts.get(i) != null &&  AppgridHelper.appGridCardConfiguration.get(cardProducts.get(i).asString()) != null 
                                                        && AppgridHelper.appGridCardConfiguration.get(cardProducts.get(i).asString())!= null 
							&& !AppgridHelper.appGridCardConfiguration.get(cardProducts.get(i).asString()).isNull())
						cardsArray = cardsArray.add(AppgridHelper.appGridCardConfiguration.get(cardProducts.get(i).asString()));
					}
				}
				}
				if (!airtel) {
					for (int k = 0; k < airtelProds.size(); k++) {
						if (productKey.equalsIgnoreCase(airtelProds.get(k).asString())) {
//							railsArray = new JsonArray();
							cardsArray = new JsonArray();
						}
					}
				}

			} else {
				for (int i = 0; i < 10; i++) {
					railsJsonArray.add("rails_" + i);
					productsJsonArray.add("products_" + i);
				}
			}

			/* Get Personalized Card IDs */
			try {
//				personalizedRail = getPersonalizedRails(userId);
//				JsonArray personalizedRailsArray = (JsonArray) personalizedRail.get("railsIdsArray");
				
				personalizedCard = getPersonalizedRails(userId);
				JsonArray personalizedCardsArray = (JsonArray) personalizedCard.get("railsIdsArray");

				/* Add Personalized Card IDs to cardlIDs Array */
				for (int k = 0; k < personalizedCardsArray.size(); k++) {
					cardsArray.add(personalizedCardsArray.get(k).asString());
				}
			} catch (Exception e) {
				log.error("Personalized Rails Error:" + e);
			}

			JsonArray productsArray = new JsonArray();

			for (int i = 0; i < productsJsonArray.size(); i++) {
				String productId = productsJsonArray.get(i).asString();
				if (SubscriptionHelper.allProductsMap.get(productId) != null
						&& !SubscriptionHelper.allProductsMap.get(productId).isEmpty()) {
					JsonObject prodObj = SubscriptionHelper.allProductsMap.get(productId).asObject();
					productsArray.add(prodObj);
				}
			}   

			Boolean giftFlag = false;
			String giftProducts = AppgridHelper.appGridMetadata.get("gift_products_def").asObject()
					.get("all_gift_products").asString();

			for (int i = 0; i < productsArray.size(); i++) {
				if (giftProducts.contains(productsArray.get(i).asObject().get("id").asString())) {
					giftFlag = true;
				}

			}
			if (userProfileDao.getUserProfileByUserId(userId) == null || !userProfileDao.getHooqTrialFlag(userId)) {
				giftFlag = true;
				JsonValue prodObj = SubscriptionHelper.allProductsMap.get(
						AppgridHelper.appGridMetadata.get("gift_products_def").asObject().get("hooq_prepaid_prod_id")
								.asString());
                                if (prodObj != null)
                                    productsArray.add(prodObj.asObject());
			}
		
                        String bsbAvailableOffers = SubscriptionHelper.getavailableOffer(userId, deviceId, deviceOs, appVersion);
                        JsonObject offerResponse = JsonObject.readFrom(bsbAvailableOffers);
                        if (offerResponse.get("offerStatus").asArray() != null && offerResponse.get("offerStatus").asArray().size() > 0) {
                            JsonArray offerstatus = offerResponse.get("offerStatus").asArray();
                            for (int i = 0; i < offerstatus.size(); i++) {//"offerId":9013
                    if((offerstatus.get(i).asObject().get("offerId").asInt() == 9013
                            || offerstatus.get(i).asObject().get("offerId").asInt() == 9011)
                            &&
                                        offerstatus.get(i).asObject().get("packs").asArray() != null &&
                                        offerstatus.get(i).asObject().get("packs").asArray().size() > 0) {
                                    JsonArray offerpacks = offerstatus.get(i).asObject().get("packs").asArray();
                                            
                                    for (int j = 0; j < offerpacks.size(); j++) {
                                        String offerId = offerpacks.get(j).asObject().get("partnerProductId").asString();
                                        String action = offerpacks.get(j).asObject().get("action").asString();
                                        if (offerId.equalsIgnoreCase(AppgridHelper.appGridMetadata.get("gift_products_def").asObject().get("livetv_single_prod_id")
								.asString()) && action.equalsIgnoreCase("PRE_AUTH")) {
                                            giftFlag = true;
                                            productsJsonArray.add(offerId);
                                            if (AppgridHelper.appGridCardConfiguration.get(offerId) != null) {
                                                cardsArray.add(AppgridHelper.appGridCardConfiguration.get(offerId));
                                            }
                                            if (SubscriptionHelper.allProductsMap.get(offerId) != null) {
                                                JsonObject prodObj = SubscriptionHelper.allProductsMap.get(offerId).asObject();
                                                productsArray.add(prodObj);
                                            }
                                        }
                                    }
                                }
                            }
                        }
			
//			jsonObject.add("rails", railsArray);
                        jsonObject.add("products", productsJsonArray);
			jsonObject.add("rails", cardsArray);
			jsonObject.add("messageKeys", getMsgKeys(productsJsonArray, userId, deviceId, airtel, showOffer));
			jsonObject.add("productsArray", productsArray);
			JsonValue reorderCp = AppgridHelper.appGridMetadata.get("cp_reordering").asObject()
					.get(personalizedCard.get("cp_reorder").asString().toLowerCase());
			jsonObject.add("reordercp", reorderCp != null && !reorderCp.isNull() ? reorderCp.asString() : "");
			jsonObject.add("gift", giftFlag);

			result = jsonObject.toString();
		} catch (BusinessApplicationException e) {
			log.error("User Not Found-, line 149 - " + userId + ":", e);
			throw new BusinessApplicationException(HttpStatus.NOT_FOUND.value(), e, "User Not Found!");
		} catch (Exception e) {
			log.error("Rails Error:" + e);
			throw new BusinessApplicationException(HttpStatus.NOT_FOUND.value(), "Some error occured!");
		}
		return result;	
	}
}