package com.accedo.wynkstudio.service.impl;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import com.accedo.wynkstudio.dao.BundleCounterDao;
import com.accedo.wynkstudio.dao.ProductDao;
import com.accedo.wynkstudio.dao.UserProfileDao;
import com.accedo.wynkstudio.entity.Product;
import com.accedo.wynkstudio.exception.BusinessApplicationException;
import com.accedo.wynkstudio.helper.AppgridHelper;
import com.accedo.wynkstudio.helper.ProducttHelper;
import com.accedo.wynkstudio.helper.SubscriptionHelper;
import com.accedo.wynkstudio.service.CpLinkingService;
import com.accedo.wynkstudio.service.SubscriptionService;
import com.accedo.wynkstudio.util.JsonTransformation;
import com.accedo.wynkstudio.util.Util;
import com.accedo.wynkstudio.vo.BundleCounterVO;
import com.accedo.wynkstudio.vo.ProductVO;
import com.accedo.wynkstudio.vo.UserProfileVO;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

@Service
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService{
	
	private HttpHeaders headers;
	final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired		
	private UserProfileDao userProfileDao;		
			
	@Autowired		
	private ProductDao  productDao;		
			
	@Autowired		
	private BundleCounterDao bundleCounterDao;
	
	@Autowired		
	private CpLinkingService cpLinkingService ;
		
	@PostConstruct
	public void init() {
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	}
	
	/* Set entitlement for User in Appgrid and MPX */
	@Override
	public String setEntitlementsForUser(String uid, String product, String contextPath, String token, String cpId) {
		String response = "";
		try{
		cpId = (cpId != null && !cpId.isEmpty()) ? cpId : "EROSNOW";
		switch (cpId.toLowerCase()) {
		case "singtel":
			System.out.print(AppgridHelper.appGridMetadata.get("bsb_flag").toString());
			UserProfileVO userProfileVOSingtel = userProfileDao.getUserLoginDetails(uid);
			if (AppgridHelper.appGridMetadata.get("bsb_flag").toString().equalsIgnoreCase("\"true\"")) {
				String tokenString = JsonObject.readFrom(token).get("token").asString();
				String bsbResponse = SubscriptionHelper.checkPackStatus(uid, tokenString, headers);
				JsonObject statusObject = JsonObject.readFrom(bsbResponse);
				JsonObject productObject = (statusObject.get(product) != null && !statusObject.get(product).isNull()) ? statusObject
						.get(product).asObject() : null;
				if (productObject != null && !productObject.isNull()) {
					JsonObject subscriptionStatus = new JsonObject();
					subscriptionStatus = SubscriptionHelper.evaluateSubscriptionStatus(productObject, product);
					long bsbValidity = productObject.get("expireTimestamp").asLong();
					
					long monthInMs = 0;
					long contentValidity = System.currentTimeMillis() + monthInMs;
					int productCycle = SubscriptionHelper.allProductsMap.get(product).asObject().get("productCycle").asInt();
					if(productCycle == 1)
					{
						monthInMs = (long) 86400000.00;
					}
					else if(productCycle == 7)
					{
						monthInMs = (long) 604800000.00;
					}
					else
					{
						monthInMs = (long) 2592000000.00;
					}
					
					contentValidity = System.currentTimeMillis() + monthInMs;
					
					
					boolean bundleFlag = (SubscriptionHelper.allProductsMap.get(product).asObject().get("bundleFlag") != null && !SubscriptionHelper.allProductsMap.get(product).asObject().get("bundleFlag").isNull()) ? SubscriptionHelper.allProductsMap.get(product).asObject().get("bundleFlag").asBoolean() : false;
					String productType = SubscriptionHelper.allProductsMap.get(product).asObject().get("productType").asString();
					if (subscriptionStatus.get("status").asBoolean()) {
						addProduct(uid, product, cpId, productType, bundleFlag, bsbValidity, contentValidity, headers, subscriptionStatus.get("body").asObject());
						response = SubscriptionHelper.createSingtelUser(uid, product, userProfileVOSingtel.getEmail(), headers);
						JsonObject reponseJsonObject = JsonObject.readFrom(response);
						if(reponseJsonObject.get("status").asInt() == 1 ){
							productDao.updateProductActivated(uid, product);
						//	log.info("Activating product :" + product + " for uid:" + uid);
						}
						productDao.updateProductActive(uid, product, true);
						setSingtelSubscription(uid, product, productObject, subscriptionStatus.get("body").asObject());
						response = "{\"code\":200, \"message\":\"Subscription Successful\"}";
					} else {
						deleteProduct(uid, product);
						productDao.updateProductLiv(uid, cpId, true);
						response = "{\"code\":200, \"message\":\"Subscription Failure\"}";
					}
				} else {
					deleteProduct(uid, product);
					productDao.updateProductLiv(uid, cpId, true);
					response = "{\"code\":200, \"message\":\"Subscription Failure\"}";
				}
			} else {
				response = SubscriptionHelper.createSingtelUser(uid, product, userProfileVOSingtel.getEmail(), headers);
				JsonObject reponseJsonObject = JsonObject.readFrom(response);
				if(reponseJsonObject.get("status").asString().equalsIgnoreCase("1")){
					productDao.updateProductActivated(uid, product);
				}
				setSingtelSubscription(uid, product, null, null);
				productDao.updateProductActive(uid, product, true);
			}
			break;
		default:
				JsonObject tokenObject = JsonObject.readFrom(token).asObject();
				String tokenString = tokenObject.get("token").asString();

				if (AppgridHelper.appGridMetadata.get("bsb_flag").toString().equalsIgnoreCase("\"true\"")) {
					String bsbResponse = SubscriptionHelper.checkPackStatus(uid, tokenString, headers);
					JsonObject statusObject = JsonObject.readFrom(bsbResponse);
					JsonObject productObject = (statusObject.get(product) != null && !statusObject.get(product)
							.isNull()) ? statusObject.get(product).asObject() : null;
					if (productObject != null && !productObject.isNull()) {
						JsonObject subscriptionStatus = new JsonObject();
						subscriptionStatus = SubscriptionHelper.evaluateSubscriptionStatus(productObject, product);
						long bsbValidity = productObject.get("expireTimestamp").asLong();
						
						long monthInMs = 0;
						long contentValidity = System.currentTimeMillis() + monthInMs;
						int productCycle = SubscriptionHelper.allProductsMap.get(product).asObject().get("productCycle").asInt();
						if(productCycle == 1)
						{
							monthInMs = (long) 86400000.00;
						}
						else if(productCycle == 7)
						{
							monthInMs = (long) 604800000.00;
						}
						else
						{
							monthInMs = (long) 2592000000.00;
						}
						
						contentValidity = System.currentTimeMillis() + monthInMs;
						
						boolean bundleFlag = (SubscriptionHelper.allProductsMap.get(product).asObject().get("bundleFlag") != null && !SubscriptionHelper.allProductsMap.get(product).asObject().get("bundleFlag").isNull()) ? SubscriptionHelper.allProductsMap.get(product).asObject().get("bundleFlag").asBoolean() : false;
						String productType = SubscriptionHelper.allProductsMap.get(product).asObject().get("productType").asString();
						if (subscriptionStatus.get("status").asBoolean()) {
							if (cpId.equalsIgnoreCase("EROSNOW")) {
								addProduct(uid, product, cpId, productType, bundleFlag, bsbValidity, contentValidity, headers, subscriptionStatus.get("body").asObject());

								// Purchase in EROSNOW 
								UserProfileVO userProfileVO = userProfileDao.getUserLoginDetails(uid);
								JsonObject planObject = JsonObject.readFrom(AppgridHelper.appGridMetadata.get(
										"erosnow_product_plan").asString());
								String plan = planObject.get(product).asString();
								try
								{
									cpLinkingService.purchase(uid, plan, userProfileVO);
								}
								catch(Exception e)
								{
									log.error("Error in EROSNOW Purchase during subscription:", e);
								}
								productDao.updateProductActive(uid, product, true);
								
								response = "{\"code\":200, \"message\":\"Subscription Successful\"}";
							} else {
								addProduct(uid, product, cpId, productType, bundleFlag, bsbValidity, contentValidity, headers, subscriptionStatus.get("body").asObject());
								productDao.updateProductActive(uid, product, true);
								response = "{\"code\":200, \"message\":\"Subscription Successful\"}";
							}
						} else {
							deleteProduct(uid, product);
							productDao.updateProductLiv(uid, cpId, true);
							response = "{\"code\":200, \"message\":\"Subscription Failure\"}";
						}
					} else {
						deleteProduct(uid, product);
						productDao.updateProductLiv(uid, cpId, true);
						response = "{\"code\":200, \"message\":\"Subscription Failure\"}";
					}
				} else {
					addProduct(uid, product, cpId, "prime", true, System.currentTimeMillis(), System.currentTimeMillis(), headers, null);
					response = "{\"code\":200, \"message\":\"Subscription Successful\"}";
				}
			break;
		}
		}
		catch (BusinessApplicationException e)
		{
			log.error("Error From BSB: ", e);
			throw new BusinessApplicationException(HttpStatus.FAILED_DEPENDENCY.value(), "Error From BSB PackStatus Call");
		}
		catch (Exception e)
		{
			log.error("Subscription Error Log: ", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}

		return response;
	}
	
	/* Set entitlement for User in Appgrid and MPX */
	@Override
	public String setEntitlementsByBsb(String uid, String product, String contextPath, String token, String cpId) {
		String response = "";
		try{
		cpId = (cpId != null && !cpId.isEmpty()) ? cpId : "EROSNOW";
		cpId = (cpId == "HOOQ" )? "SINGTEL": cpId;
		switch (cpId.toLowerCase()) {
		case "singtel":
			System.out.print(AppgridHelper.appGridMetadata.get("bsb_flag").toString());
			UserProfileVO userProfileVOSingtel = userProfileDao.getUserLoginDetails(uid);
			if (AppgridHelper.appGridMetadata.get("bsb_flag").toString().equalsIgnoreCase("\"true\"")) {
					JsonObject subscriptionStatus = new JsonObject();
					JsonObject responseObject = new JsonObject();
					responseObject.add("status", "ACTIVE");
					responseObject.add("allowPlayback", true);
					responseObject.add("unsubscribe", true);
					responseObject.add("subscribe", false);
					subscriptionStatus.set("status", true);
					subscriptionStatus.set("body", responseObject);
					
					long monthInMs = 0;
					long contentValidity = System.currentTimeMillis() + monthInMs;
					int productCycle = SubscriptionHelper.allProductsMap.get(product).asObject().get("productCycle").asInt();
					if(productCycle == 1)
					{
						monthInMs = (long) 86400000.00;
					}
					else if(productCycle == 7)
					{
						monthInMs = (long) 604800000.00;
					}
					else
					{
						monthInMs = (long) 2592000000.00;
					}
					
					contentValidity = System.currentTimeMillis() + monthInMs;
					
					
					boolean bundleFlag = (SubscriptionHelper.allProductsMap.get(product).asObject().get("bundleFlag") != null && !SubscriptionHelper.allProductsMap.get(product).asObject().get("bundleFlag").isNull()) ? SubscriptionHelper.allProductsMap.get(product).asObject().get("bundleFlag").asBoolean() : false;
					String productType = SubscriptionHelper.allProductsMap.get(product).asObject().get("productType").asString();
						
						addProduct(uid, product, cpId, productType, bundleFlag, contentValidity, contentValidity, headers, subscriptionStatus.get("body").asObject());
						response = SubscriptionHelper.createSingtelUser(uid, product, userProfileVOSingtel.getEmail(), headers);
						JsonObject reponseJsonObject = JsonObject.readFrom(response);
						if(reponseJsonObject.get("status").asInt() == 1 ){
							productDao.updateProductActivated(uid, product);
							//log.info("Activating product :" + product + " for uid:" + uid);
						}
						productDao.updateProductActive(uid, product, true);
						setSingtelSubscription(uid, product, null, subscriptionStatus.get("body").asObject());
						response = "{\"code\":200, \"message\":\"Subscription Successful\"}";

			} else {
				response = SubscriptionHelper.createSingtelUser(uid, product, userProfileVOSingtel.getEmail(), headers);
				JsonObject reponseJsonObject = JsonObject.readFrom(response);
				if(reponseJsonObject.get("status").asString().equalsIgnoreCase("1")){
					productDao.updateProductActivated(uid, product);
				}
				setSingtelSubscription(uid, product, null, null);
				productDao.updateProductActive(uid, product, true);
			}
			break;
		default:
				if (AppgridHelper.appGridMetadata.get("bsb_flag").toString().equalsIgnoreCase("\"true\"")) {
						JsonObject subscriptionStatus = new JsonObject();
						JsonObject responseObject = new JsonObject();
						responseObject.add("status", "ACTIVE");
						responseObject.add("allowPlayback", true);
						responseObject.add("unsubscribe", true);
						responseObject.add("subscribe", false);
						subscriptionStatus.set("status", true);
						subscriptionStatus.set("body", responseObject);

						long monthInMs = 0;
						long contentValidity = System.currentTimeMillis() + monthInMs;
						int productCycle = SubscriptionHelper.allProductsMap.get(product).asObject().get("productCycle").asInt();
						if(productCycle == 1)
						{
							monthInMs = (long) 86400000.00;
						}
						else if(productCycle == 7)
						{
							monthInMs = (long) 604800000.00;
						}
						else
						{
							monthInMs = (long) 2592000000.00;
						}
						
						contentValidity = System.currentTimeMillis() + monthInMs;
						
						boolean bundleFlag = (SubscriptionHelper.allProductsMap.get(product).asObject().get("bundleFlag") != null && !SubscriptionHelper.allProductsMap.get(product).asObject().get("bundleFlag").isNull()) ? SubscriptionHelper.allProductsMap.get(product).asObject().get("bundleFlag").asBoolean() : false;
						String productType = SubscriptionHelper.allProductsMap.get(product).asObject().get("productType").asString();
							if (cpId.equalsIgnoreCase("EROSNOW")) {
								addProduct(uid, product, cpId, productType, bundleFlag, contentValidity, contentValidity, headers, subscriptionStatus.get("body").asObject());

								// Purchase in EROSNOW 
								UserProfileVO userProfileVO = userProfileDao.getUserLoginDetails(uid);
								JsonObject planObject = JsonObject.readFrom(AppgridHelper.appGridMetadata.get(
										"erosnow_product_plan").asString());
								String plan = planObject.get(product).asString();
								try
								{
									cpLinkingService.purchase(uid, plan, userProfileVO);
								}
								catch(Exception e)
								{
									log.error("Error in EROSNOW Purchase during subscription:", e);
								}
								productDao.updateProductActive(uid, product, true);
								
								response = "{\"code\":200, \"message\":\"Subscription Successful\"}";
							} else {
								addProduct(uid, product, cpId, productType, bundleFlag, contentValidity, contentValidity, headers, subscriptionStatus.get("body").asObject());
								productDao.updateProductActive(uid, product, true);
								response = "{\"code\":200, \"message\":\"Subscription Successful\"}";
							}
				} else {
					addProduct(uid, product, cpId, "prime", true, System.currentTimeMillis(), System.currentTimeMillis(), headers, null);
					response = "{\"code\":200, \"message\":\"Subscription Successful\"}";
				}
			break;
		}
		}
		catch (BusinessApplicationException e)
		{
			log.error("Error From BSB: ", e);
			throw new BusinessApplicationException(HttpStatus.FAILED_DEPENDENCY.value(), "Error From BSB PackStatus Call");
		}
		catch (Exception e)
		{
			log.error("Subscription Error Log: ", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}

		return response;
	}
	
	/* Unsubscribe User */
	@Override
	public String unsubscribeUser(String uid, String product, String contextPath, String token, String cpId) {
		String response = "Unsubscribed";
		try {
		cpId = (cpId != null && !cpId.isEmpty()) ? cpId : "EROSNOW";
		switch (cpId.toLowerCase()) {
		case "singtel":
			if (AppgridHelper.appGridMetadata.get("bsb_flag").toString().equalsIgnoreCase("\"true\"")) {
				SubscriptionHelper.unsubscribe(uid, product, token, headers);
			}
			break;
		default:
			
			if (AppgridHelper.appGridMetadata.get("bsb_flag").toString().equalsIgnoreCase("\"true\"")) {
				SubscriptionHelper.unsubscribe(uid, product, token, headers);
			}

			deleteProduct(uid, product);
			productDao.updateProductLiv(uid, cpId, true);
		}
		}
		catch (Exception e)
		{
			log.error("Error, line 247 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return response;
	}

	@Override
	public String getPackStatus(String uid, String token, String providerIds) {

		String response = "";
		try {
			providerIds = providerIds.replace(",", "|");

			String productId = "";
			List<String> productArray = new ArrayList<String>();
			String appendIds = "";
			String pId = "";
			ProductVO productFromDb = null;
			HashMap<String, String> expiryMap = new HashMap<String, String>();
			HashMap<String, Boolean> buttonMap = new HashMap<String, Boolean>();

			if (AppgridHelper.appGridMetadata.get("bsb_flag").toString().equalsIgnoreCase("\"true\"")) {
				String bsbToken = JsonObject.readFrom(token).get("token").asString();
				String bsbResponse = SubscriptionHelper.checkPackStatus(uid, bsbToken, headers);
				JsonObject statusObject = JsonObject.readFrom(bsbResponse);

				for (int i = 0; i < SubscriptionHelper.allProductsArray.size(); i++) {
					productId = SubscriptionHelper.allProductsArray.get(i).asObject().get("id").asString();
					JsonObject bsbProductObject = (statusObject.get(productId) != null && !statusObject.get(productId)
							.isNull()) ? statusObject.get(productId).asObject() : null;
					if (bsbProductObject != null && !bsbProductObject.isNull()) {
						JsonObject subscriptionStatus = new JsonObject();
						subscriptionStatus = SubscriptionHelper.evaluateSubscriptionStatus(bsbProductObject, productId);
						if (subscriptionStatus.get("status").asBoolean()) {
							productFromDb = productDao.getProductByUserWithProductId(productId, uid);
                                                        if (productId.equalsIgnoreCase(AppgridHelper.appGridMetadata.get("gift_products_def").asObject().get("livetv_single_prod_id")
								.asString()) && productFromDb != null) {
                                                            productFromDb.setActive(true);
                                                        }
							if (productFromDb != null && productFromDb.getActive()) {
								productArray.add(productId);
								appendIds = appendIds + productId + "|";
								Long expiryTimeInMs = bsbProductObject.get("expireTimestamp").asLong();
								Date expiryDate = new Date(expiryTimeInMs);
								String output = "";
								Long validityLeft = expiryTimeInMs / 1000L - System.currentTimeMillis() / 1000L;
								if (validityLeft > 0 && validityLeft <= 172800) {
									output = String.valueOf((validityLeft / 3600)) + " hours";
								} else {
									DateFormat outputFormatter = new SimpleDateFormat("dd/MM/yyyy");
									output = outputFormatter.format(expiryDate);
								}
								if(productFromDb.getContentValidity() < productFromDb.getBsbValidity())
								{
									Date expiryDateCp = new Date(productFromDb.getContentValidity());
									DateFormat outputFormatter = new SimpleDateFormat("dd/MM/yyyy");
									expiryMap.put(productId, outputFormatter.format(expiryDateCp).toString());
								}
								else
								{
									expiryMap.put(productId, output.toString());
								}		
								buttonMap.put(productId, subscriptionStatus.get("body").asObject().get("unsubscribe")
										.asBoolean());
							}
						}
					}
				}
			} else {
				List<String> productIds = productDao.getProductIdsByUserId(uid);
				for (String product : productIds) {
					productArray.add(product);
					appendIds = appendIds + product + "|";
				}
			}

			if (!appendIds.isEmpty()) {
				String feedsString = AppgridHelper.appGridMetadata.get("accounts_plans_feed").asString()
						+ "?byProductTags=" + providerIds + "&byGuid=" + appendIds;
				response = Util.executeApiGetCall(feedsString);
				response = response.replace("pl2$bundleFlag", "bundleFlag").replace("pl2$bundleLimit", "bundleLimit")
						.replace("pl2$isSubscription", "isSubscription");
				response = response.replace("pl1$productType", "productType").replace("pl2$productCycle", "productCycle").replace("pl1$freePack", "freePack");
				response = JsonTransformation.transformJson(response, "/jsonSpec/mpx/plans.json");
				JsonObject responseJson = JsonObject.readFrom(response);
				JsonArray responseArray = responseJson.get("entries").asArray();
				for (int i = 0; i < responseArray.size(); i++) {
					JsonArray cpArray = responseArray.get(i).asObject().get("productTags").asArray();
					String cp = "";
					for (int j = 0; j < cpArray.size(); j++) {
						if (cpArray.get(j).isObject() && cpArray.get(j).asObject().get("scheme").asString().equalsIgnoreCase("provider")) {
							cp = cpArray.get(j).asObject().get("title").asString();

						}
					}
					String limit = "";
					pId = responseArray.get(i).asObject().get("id").asString();
					responseArray.get(i).asObject().set("contentProvider", cp.toUpperCase());
					responseArray.get(i).asObject().set("expiry", expiryMap.get(pId));
					responseArray.get(i).asObject()
							.set("enabled", buttonMap.get(pId) != null ? buttonMap.get(pId) : true);
					if (SubscriptionHelper.bundleCheck(pId)) {
						limit = getBundleStatus(pId, uid);
					}  
                                        
                                        if (responseArray.get(i).asObject().get("longDescription") != null) {
                                            String[] longDescription = responseArray.get(i).asObject().get("longDescription").asString().split("~");
                                            JsonArray longDescriptionJsonArray = new JsonArray();
                                            if (longDescription.length > 0) {
                                                    for (int d = 0; d < longDescription.length; d++) {
                                                            if (!longDescription[d].isEmpty()) {
                                                                    longDescriptionJsonArray.add(longDescription[d]);
                                                            }
                                                    }
                                            }
                                            responseArray.get(i).asObject().set("longDescription", longDescriptionJsonArray);
                                        }
					responseArray.get(i).asObject().set("bundleCounter", limit);
				}
				responseJson.set("entries", responseArray);
				response = responseJson.toString();
			} else {
				response = "{\"entries\":[]}";
			}
		} catch (BusinessApplicationException e) {
			log.error("Error, line 355 -", e);
			throw new BusinessApplicationException(e.getMsgId(), "Forbidden Response from BSB");
		}catch (Exception e) {
			log.error("Error, line 358 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}

		return response;
	}
	
	private Boolean setSingtelSubscription(String userId, String SKU, JsonObject statusObject, JsonObject subscriptionStatus)
	{
		userProfileDao.updateUserProfileCreatedflag(userId, true);
		return true;
	}
	
	private String getBundleStatus(String productId, String uid) {
		String response = "";
		BundleCounterVO bundleCounterVO = bundleCounterDao.getBundleCounterByUserWithProductId(productId, uid);
		int limit = bundleCounterVO.getItemLimit();
		int count = (int) bundleCounterVO.getCounter();
		response = (limit - count) + " out of " + limit + " movies left";
		return response;
	}

	private void addProduct(String userId, String productId, String cpId, String productType, Boolean bundleFlag, Long bsbValidity, Long contentValidity, HttpHeaders headers,
			JsonObject statusObject) {
		deleteProduct(userId, productId);
		productDao.updateProductLiv(userId, cpId, false);
		Product product = ProducttHelper.toProductWithDiffernetFields(userId, productId, cpId, productType, bundleFlag, bsbValidity,
				contentValidity, statusObject);
	    productDao.createProduct(product);
		
	}

	private void deleteProduct(String userId, String productId) {
		Product product;
		ProductVO productVO = productDao.getProductByUserWithProductId(productId, userId);
	    if(productVO != null){
	    	product = new Product();
	    	product.setId(Long.parseLong(productVO.getId()));
	    	productDao.deleteProduct(product);
	    }   
	    BundleCounterVO bundleCounterVO = bundleCounterDao.getBundleCounterByUserWithProductId(productId, userId);
	    if(bundleCounterVO != null)
	    {
	    	bundleCounterDao.deleteBundleCounter(bundleCounterVO.getId());
	    }
	}
	
	private String revokeSingtelEntitlement(String userId, String SKU) {
		String response = "";
		String url = AppgridHelper.appGridMetadata.get("hooq_revokeUser_api").asString();
		String requestBody = "{}";
		String secret = "123#abcyjsd";
		String password = Util.getMD5Hash(userId + secret);
		try {
			url = url.replace("{0}", userId).replace("{1}", SKU).replace("{2}", password);
			response = Util.executeApiPostCall(url, headers, requestBody);
			JsonObject responseJson = JsonObject.readFrom(response);
			JsonObject rspObj = responseJson.get("RevokeEntitlementResponseMessage").asObject();
			String code = rspObj.get("responseCode").asString();
			if (code.equals("1") || code.equals("2")) {
				deleteProduct(userId, SKU);
				userProfileDao.updateUserProfileCreatedflag(userId, false);
			} else if (code.equals("0")) {
				if (responseJson.get("RevokeEntitlementResponseMessage").asObject().get("failureMessage").asObject()
						.get("errorMessage").asString().equalsIgnoreCase("UserID Not Found")) {
					deleteProduct(userId, SKU);
					userProfileDao.updateUserProfileCreatedflag(userId, false);
				} else {
					log.error("Evergent Revoke Entitlement failed, Response - ", response);
					throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
							"Create User Failed!");
				}
			} else {
				log.error("Evergent Revoke Entitlement failed, Response - ", response);
				throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Revoke entitlement failed!");
			}
		} catch (HttpClientErrorException e) {
			log.error("Error, line 441 -", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (Exception e) {
			log.error("Error, line 444 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		}
		return response;
	}

	@Override
	public String activateProduct(String uid, String tokenString, String productId, String cpId, String deviceId, String platform, String deviceOs, String appVersion) {
            String response = "Activated";
            try {
                
                    response = SubscriptionHelper.activateProduct(uid, productId, deviceId, deviceOs, appVersion, headers);
                    if(!platform.isEmpty() && platform.equalsIgnoreCase("ios"))
                    {
                        userProfileDao.setHooqTrialFlagIos(uid);
                    }
                    else
                    {
                        userProfileDao.setHooqTrialFlag(uid);
                    }
            } catch (BusinessApplicationException e)
            {
                log.error("Error From BSB: ", e);
                throw new BusinessApplicationException(HttpStatus.FAILED_DEPENDENCY.value(), "Error From BSB Provision Call");
            }
            catch (Exception e)
            {
                log.error("Subscription Error Log: ", e);
                throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
            }
            return response;
	}

}
