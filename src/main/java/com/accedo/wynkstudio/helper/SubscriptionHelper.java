package com.accedo.wynkstudio.helper;

import java.security.SignatureException;
import java.util.Arrays;
import java.util.HashMap;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

import com.accedo.wynkstudio.exception.BusinessApplicationException;
import com.accedo.wynkstudio.util.JsonTransformation;
import com.accedo.wynkstudio.util.Signature;
import com.accedo.wynkstudio.util.Util;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

@SuppressWarnings("restriction")
public class SubscriptionHelper {
	final static Logger log = LoggerFactory.getLogger("SubscriptionHelper");
	public static HashMap<String, JsonArray> productsList = null;
	public static HashMap<String, JsonObject> allProductsMap = null;
	public static JsonArray allProductsArray = null;

	public static String getUserProfile(String uid, HttpHeaders header) {
		String response = "";
		String bsbUrl = AppgridHelper.appGridMetadata.get("bsb_profile_url").asString().replace("{0}", uid);
		String fromRequiest = AppgridHelper.appGridMetadata.get("bsb_profile_signature_data").asString().replace("{0}", uid);
		String tokenSecret = AppgridHelper.appGridMetadata.get("bsb_provision_secrete_key").asString();
		String token = AppgridHelper.appGridMetadata.get("bsb_provision_app_auth_key").asString();
		HttpHeaders bsbHeader = new HttpHeaders();
		char col = ':';
		try {
			String currentTime = String.valueOf(System.currentTimeMillis());
			fromRequiest = fromRequiest + currentTime;
			String signature = Signature.calculateRFC2104HMAC(fromRequiest, tokenSecret);
			bsbHeader.setContentType(MediaType.APPLICATION_JSON);
			bsbHeader.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			bsbHeader.set("x-bsy-atkn", token + col + signature);
			bsbHeader.set("x-bsy-date", currentTime);
			response = Util.executeApiGetCall(bsbUrl, bsbHeader);
			log.info("BSB Response for Profile API for uid:" + uid + ", body:" + response);
		} catch (HttpClientErrorException e) {
			log.error("Error From BSB in Profile API: Code=" + e.getStatusCode().value() + ", Message="
					+ e.getStatusText());
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (SignatureException e) {
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e,
					"System encountered a Signature Exception");
		} catch (Exception e) {
			log.error("Error From BSB in PackStatus API: " + e);
			throw new BusinessApplicationException(HttpStatus.FAILED_DEPENDENCY.value(), e);
		}
		return response;
	}

	public static String getOfferProvision(String uid, String offerIds, String deviceId, HttpHeaders header) {
		String response = "";
		String bsbUrl = AppgridHelper.appGridMetadata.get("bsb_offer_url").asString();
		String secretKey = AppgridHelper.appGridMetadata.get("bsb_provision_secrete_key").asString();
		String appKey = AppgridHelper.appGridMetadata.get("bsb_provision_app_auth_key").asString();
		String fromRequiest = AppgridHelper.appGridMetadata.get("bsb_offer_signature_data").asString();
		HttpHeaders bsbHeader = new HttpHeaders();
		try {
			String currentTime = String.valueOf(System.currentTimeMillis());
			String payload = AppgridHelper.appGridMetadata.get("bsb_provision_offer_payload_data").asString();
			payload = payload.replace("{0}", uid).replace("{1}", "").replace("{2}", offerIds).replace("{3}", deviceId);
			fromRequiest = fromRequiest + payload + currentTime;
			String signature = Signature.calculateRFC2104HMAC(fromRequiest, secretKey);
			char col = ':';
			bsbHeader.setContentType(MediaType.APPLICATION_JSON);
			bsbHeader.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			bsbHeader.set("x-bsy-atkn", appKey + col + signature);
			bsbHeader.set("x-bsy-date", currentTime);
			response = Util.executeApiPostCall(bsbUrl, bsbHeader, payload);
			log.info("BSB Response for GetOffer API for uid:" + uid + ", body:" + response);
		} catch (HttpClientErrorException e) {
			log.error("Error From BSB in GetOffer API: Code=" + e.getStatusCode().value() + ", Message="
					+ e.getStatusText());
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (SignatureException e) {
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e,
					"System encountered a Signature Exception");
		} catch (Exception e) {
			log.error("Error From BSB in GetOffer API: " + e);
			throw new BusinessApplicationException(HttpStatus.FAILED_DEPENDENCY.value(), e);
		}
		return response;
	}

	public static String updateMongageEvent(HttpHeaders headers, String payload) throws SignatureException {
		String response = "";
		String userName = AppgridHelper.appGridMetadata.get("moengage_app_id").asString();
		String bsbUrl = AppgridHelper.appGridMetadata.get("moengage_api_url").asString().replace("{0}", userName);
		String password = AppgridHelper.appGridMetadata.get("moengage_secret_key").asString();
		try {
			String userCredentials = userName + ":" + password;
			new Base64();
			String basicAuth = "Basic " + new String(Base64.encode(userCredentials.getBytes()));
			headers.set("Authorization", basicAuth);
			response = Util.executeApiPostCall(bsbUrl, headers, payload);
		} catch (HttpClientErrorException e) {
			log.error("Error From Mo-Engage API: Code=" + e.getStatusCode().value() + ", Message=" + e.getStatusText());
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (Exception e) {
			log.error("Error From Mo-Engage API: " + e);
			throw new BusinessApplicationException(HttpStatus.FAILED_DEPENDENCY.value(), e);
		}
		return response;
	}

	public static String activateProduct(String uid, String productId, HttpHeaders header) {
            if (productId.equalsIgnoreCase("16000")) {
                String offer_Ids = "[16000]";
                return SubscriptionHelper.getOfferProvision(uid, offer_Ids, uid, header);
            }
		String response = "";
		String bsbUrl = AppgridHelper.appGridMetadata.get("bsb_subscription_url").asString();
		HttpHeaders bsbHeader = new HttpHeaders();
		try {
			productId = AppgridHelper.bsbProvisioningProductMap.get(productId) != null
					&& !AppgridHelper.bsbProvisioningProductMap.get(productId).isNull() ? AppgridHelper.bsbProvisioningProductMap
					.get(productId).asString() : productId;
			String orderId = RandomStringUtils.random(32, 0, 20, true, true, "bj81G5RDED3DC6142kasok".toCharArray());
			String currentTime = String.valueOf(System.currentTimeMillis());
			String payload = AppgridHelper.appGridMetadata.get("bsb_provision_activate_payload_data").asString();
			payload = payload.replace("{0}", uid).replace("{1}", productId).replace("{2}", orderId)
					.replace("{3}", currentTime);
			bsbUrl = AppgridHelper.appGridMetadata.get("bsb_provision_activate_base_url").asString();
			String fromRequiest = AppgridHelper.appGridMetadata.get("bsb_provision_activate_singnature_data")
					.asString();
			fromRequiest = fromRequiest + payload + currentTime;
			String signature = Signature.calculateRFC2104HMAC(fromRequiest,
					AppgridHelper.appGridMetadata.get("bsb_provision_secrete_key").asString());
			char col = ':';
			bsbHeader.setContentType(MediaType.APPLICATION_JSON);
			bsbHeader.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			bsbHeader.set("x-bsy-atkn", AppgridHelper.appGridMetadata.get("bsb_provision_app_auth_key").asString() + col
					+ signature);
			bsbHeader.set("x-bsy-date", currentTime);
			response = Util.executeApiPostCall(bsbUrl, bsbHeader, payload);
			log.info("BSB Response for Packstatus API for uid:" + uid + ", body:" + response);
		} catch (HttpClientErrorException e) {
			log.error("Error From BSB in PackStatus API: Code=" + e.getStatusCode().value() + ", Message="
					+ e.getStatusText());
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (SignatureException e) {
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e,
					"System encountered a Signature Exception");
		} catch (Exception e) {
			log.error("Error From BSB in PackStatus API: " + e);
			throw new BusinessApplicationException(HttpStatus.FAILED_DEPENDENCY.value(), e);
		}
		return response;
	}

	/* Check Subscription status of user from BSB */
	public static String checkPackStatus(String uid, String accessToken, HttpHeaders header) {
		String response = "";
		String bsbUrl = AppgridHelper.appGridMetadata.get("bsb_subscription_url").asString();
		HttpHeaders bsbHeader = new HttpHeaders();
		try {
			String fromRequiest = AppgridHelper.appGridMetadata.get("bsb_subscription_status_signature_data")
					.asString();
			String signature = Signature.calculateRFC2104HMAC(fromRequiest, accessToken);
			char col = ':';
			bsbHeader.setContentType(MediaType.APPLICATION_JSON);
			bsbHeader.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			bsbHeader.set("x-bsy-utkn", uid + col + signature);
			response = Util.executeApiGetCall(bsbUrl, bsbHeader);
			log.info("BSB Response for Packstatus API for uid:" + uid + ", body:" + response);
		} catch (HttpClientErrorException e) {
			log.error("Error From BSB in PackStatus API in Activate Call: Code=" + e.getStatusCode().value()
					+ ", Message=" + e.getStatusText());
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (SignatureException e) {
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e,
					"System encountered a Signature Exception");
		} catch (Exception e) {
			log.error("Error From BSB in PackStatus API: " + e);
			throw new BusinessApplicationException(HttpStatus.FAILED_DEPENDENCY.value(), e);
		}
		return response;
	}

	/* Unsubscribe a user for a particular product from BSB */
	public static String unsubscribe(String uid, String productId, String accessToken, HttpHeaders header) {
		String response = "";
		HttpHeaders bsbHeader = new HttpHeaders();
		JsonObject tokenObject = JsonObject.readFrom(accessToken);
		String token = tokenObject.get("token").asString();
		String bsbUrl = AppgridHelper.appGridMetadata.get("bsb_unsubscription_url").asString();
		bsbUrl = bsbUrl.replace("{1}", productId);
		try {
			String fromRequest = AppgridHelper.appGridMetadata.get("bsb_unsubscription_status_signature_data")
					.asString();
			fromRequest = fromRequest.replace("{1}", productId);
			String signature = Signature.calculateRFC2104HMAC(fromRequest, token);
			bsbHeader.setContentType(MediaType.APPLICATION_JSON);
			bsbHeader.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			bsbHeader.set("x-bsy-utkn", uid + ":" + signature);
			response = Util.executeApiGetCallUri(bsbUrl, bsbHeader);
			log.info("BSB Response for Unsubscribe Call for uid:" + uid + ", body:" + response);
		} catch (HttpClientErrorException e) {
			log.error("Error From BSB in Unsubscribe API: Code=" + e.getStatusCode().value() + ", Message="
					+ e.getStatusText());
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (SignatureException e) {
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e,
					"System encountered a Signature Exception");
		}
		return response;
	}

	public static JsonObject evaluateSubscriptionStatus(JsonObject product, String productId) {
		JsonObject response = new JsonObject();
		JsonObject statusResponse = subscriptionStatus(product, productId);
		if (!statusResponse.get("subscribe").asBoolean()) {
			response.set("status", true);
		} else {
			response.set("status", false);
		}
		response.set("body", statusResponse);
		return response;
	}

	private static JsonObject subscriptionStatus(JsonObject statusResponseObject, String productId) {
		long validity = statusResponseObject.get("expireTimestamp").asLong();
		JsonObject responseObject = new JsonObject();
		long currentUnixTime = System.currentTimeMillis();

		switch (statusResponseObject.get("status").asString().toLowerCase()) {
		case "never_subscribed":
			responseObject = getSubscriptionState("Never Subscribed", false, false, true);
			break;
		case "in_progress":
			responseObject = getSubscriptionState("In Progress", false, false, false);
			break;
		case "active":
			responseObject = getSubscriptionState("Active", true, true, false);
			break;
		case "prerenewal":
			responseObject = getSubscriptionState("Pre-Renewal", true, true, false);
			break;
		case "grace":
			responseObject = getSubscriptionState("Grace", true, true, false);
			break;
		case "suspended":
			responseObject = getSubscriptionState("Suspended", false, true, false);
			break;
		case "deactivated":
			if (currentUnixTime < validity) {
				if (bundleCheck(productId)) {
					responseObject = getSubscriptionState("De-Activated", false, false, true);
				} else {
					responseObject = getSubscriptionState("De-Activated", true, false, false);
				}
			} else {
				responseObject = getSubscriptionState("De-Activated", false, false, true);
			}
			break;
		default:
			break;
		}
		return responseObject;
	}

	private static JsonObject getSubscriptionState(String status, Boolean allowPlayback, Boolean unsubscribe,
			Boolean subscribe) {
		JsonObject responseObject = new JsonObject();
		responseObject.add("status", status);
		responseObject.add("allowPlayback", allowPlayback);
		responseObject.add("unsubscribe", unsubscribe);
		responseObject.add("subscribe", subscribe);
		return responseObject;
	}

	/* Get and Store list of Products for each CP */
	public static void getProducts(HttpHeaders headers) {
		String response = "";
		JsonArray productArray = new JsonArray();
		JsonArray allProductArray = new JsonArray();
		HashMap<String, JsonArray> productsMap = new HashMap<String, JsonArray>();
		HashMap<String, JsonObject> productsMapAll = new HashMap<String, JsonObject>();
		String productsUrl = AppgridHelper.appGridMetadata.get("accounts_plans_feed").asString();
		try {
			response = Util.executeApiGetCall(productsUrl);
			response = response.replace("pl2$bundleFlag", "bundleFlag").replace("pl2$bundleLimit", "bundleLimit")
					.replace("pl2$isSubscription", "isSubscription");
			response = response.replace("pl1$productType", "productType").replace("pl1$productCycle", "productCycle");
			response = JsonTransformation.transformJson(response, "/jsonSpec/mpx/plans.json");
			JsonObject responseJson = JsonObject.readFrom(response);
			JsonArray responseArray = responseJson.get("entries").asArray();
			for (JsonValue responseObject : responseArray) {
				for (JsonValue cpObject : responseObject.asObject().get("productTags").asArray()) {
					if (cpObject.asObject().get("scheme").asString().equalsIgnoreCase("provider"))
						responseObject.asObject().set("contentProvider",
								cpObject.asObject().get("title").asString().toUpperCase());

					if (productsMap.containsKey(cpObject.asObject().get("title").asString().toUpperCase())) {
						productArray = productsMap.get(cpObject.asObject().get("title").asString().toUpperCase());
						productArray.add(responseObject);
						allProductArray.add(responseObject);
						productsMapAll.put(responseObject.asObject().get("id").asString(), responseObject.asObject());
					} else {
						productArray = new JsonArray();
						productArray.add(responseObject);
						allProductArray.add(responseObject);
						productsMapAll.put(responseObject.asObject().get("id").asString(), responseObject.asObject());
					}

					productsMap.put(cpObject.asObject().get("title").asString().toUpperCase(), productArray);
				}
				productsList = productsMap;
				allProductsArray = allProductArray;
				allProductsMap = productsMapAll;
			}
		} catch (HttpClientErrorException e) {
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		}
		return;
	}

	public static Boolean bundleCheck(String productId) {
		JsonObject cpProduct = SubscriptionHelper.allProductsMap.get(productId);
		JsonValue bundle = cpProduct.asObject().get("bundleFlag");
		return (bundle != null && cpProduct.asObject().get("bundleFlag").asBoolean()) ? true : false;
	}

	public static String createSingtelUser(String userId, String SKU, String email, HttpHeaders headers) {
		String response = "";
		String url = AppgridHelper.appGridMetadata.get("hooq_createUser_api").asString();
		JsonObject productMap = JsonObject.readFrom(AppgridHelper.appGridMetadata.get("hooq_product_plan").asString());
		String product = productMap.get(SKU).asString();
		String requestBody = "{}";
		String secret = "123#abcyjsd";
		String password = Util.getMD5Hash(userId + secret);
		try {
			url = url.replace("{0}", userId).replace("{1}", product).replace("{2}", password).replace("{3}", email);
			log.info("EV Create User API Call: " + url);
			response = Util.executeApiPostCall(url, headers, requestBody);
			JsonObject responseJson = JsonObject.readFrom(response);
			log.info("EV response: " + response);
			String code = (responseJson.get("CreateUserResponseMessage").asObject().get("responseCode") != null
					&& !(responseJson.get("CreateUserResponseMessage").asObject().get("responseCode").isNull())) ? responseJson
					.get("CreateUserResponseMessage").asObject().get("responseCode").asString()
					: "1";
			if (code.equals("1") || code.equals("2")) {
				if (code.equals("1")) {
					log.info("Evergent User Creation Done For User: " + userId + ", productId=" + SKU);
				}
				log.info("Singtel User Creation success");
			} else if (code.equals("0")) {
				if (responseJson.get("CreateUserResponseMessage").asObject().get("failureMessage").asObject()
						.get("errorMessage").asString().equalsIgnoreCase("UserID Already Exists.")) {
					log.info("Singtel User Creation success for UID:" + userId + ",Timestamp:" + Util.getIST());
				} else {
					log.error("Evergent User Creation failed for UID:" + userId + ", Response - ", response);
				}
			} else {
				log.error("Evergent User Creation failed for UID:" + userId + ", Response - ", response);
			}
			code = "1";
			response = "{\"code\":200,\"status\":" + code + ",\"message\":\"Evergent User Created Successfully\"}";
		} catch (HttpClientErrorException e) {
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (Exception e) {
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e, e.getMessage());
		}
		return response;
	}

	public static Boolean checkHooqPackStatus(String userId, HttpHeaders headers) {
		Boolean response = true;
		String url = AppgridHelper.appGridMetadata.get("hooq_customer_status_url").asString();
		try {
			url = url.replace("{0}", userId);
			log.info("EV Status API Call: " + url);
			String statusResponse = Util.executeApiGetCall(url, headers);
			JsonObject responseJson = JsonObject.readFrom(statusResponse);
			log.info("EV Status APi response:" + statusResponse);
			String code = responseJson.get("GetCustomerServicesResponseMessage").asObject().get("responseCode")
					.asString();
			if (code.equals("1") || code.equals("2")) {
				response = true;
			} else if (code.equals("0")) {
				response = false;
			}
		} catch (HttpClientErrorException e) {
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (Exception e) {
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e, e.getMessage());
		}
		return response;
	}

}
