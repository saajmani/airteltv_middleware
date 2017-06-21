package com.accedo.wynkstudio.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.accedo.wynkstudio.exception.BusinessApplicationException;
import com.accedo.wynkstudio.helper.AppgridHelper;
import com.accedo.wynkstudio.helper.BundleCounterHelper;
import com.accedo.wynkstudio.helper.SubscriptionHelper;
import com.accedo.wynkstudio.service.EntitlementService;
import com.accedo.wynkstudio.util.Util;
import com.accedo.wynkstudio.util.WynkUtil;
import com.accedo.wynkstudio.vo.BundleCounterVO;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

@Service
public class EntitlementServiceImpl implements EntitlementService {

	@Autowired
	private BundleCounterDao bundleCounterDao;

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

	/*
	 * Heart Beat Call To Check Current Entitlement Status Of A User For
	 * Playback/Download
	 */
	@Override
	@Transactional
	public String checkEntitlementStatus(String contentId, String cpToken, String userId, String tokenJson) {
		String response = "";
		String productId = "";
		Boolean entitlementStatus = false;
		try {
		//	log.info("Heartbeat call for UID:" + userId + ", CP:" + cpToken + ", contentId:" + contentId);
			String token = JsonObject.readFrom(tokenJson).get("token").asString();
			JsonArray cpProducts = SubscriptionHelper.productsList.get(cpToken.toUpperCase());
			if (AppgridHelper.appGridMetadata.get("bsb_flag").toString().equalsIgnoreCase("\"true\"")) {
				String bsbResponse = SubscriptionHelper.checkPackStatus(userId, token, headers);
				for (JsonValue cpProduct : cpProducts) {
					JsonObject statusObject = JsonObject.readFrom(bsbResponse);
					JsonObject productObject = (statusObject.get(cpProduct.asObject().get("id").asString()) != null && !statusObject
							.get(cpProduct.asObject().get("id").asString()).isNull()) ? statusObject.get(
							cpProduct.asObject().get("id").asString()).asObject() : null;
					if (productObject != null && !productObject.isNull()) {
						JsonObject subscriptionStatus = new JsonObject();
						subscriptionStatus = SubscriptionHelper.evaluateSubscriptionStatus(productObject, cpProduct
								.asObject().get("id").asString());
						if (subscriptionStatus.get("body").asObject().get("allowPlayback").asBoolean()) {
							productId = cpProduct.asObject().get("id").asString();
							String productType = SubscriptionHelper.allProductsMap.get(productId).asObject().get("productType").asString();
							entitlementStatus = true;
							if (!cpProduct.asObject().getBoolean("bundleFlag", false)) {
								response = createHeartbeatResponse(false, productType, true, 0, 0, "Allowed to Playback!", false,
										productId);
								break;
							} else {
								response = createBundleHeartBeatResponse(productId, userId, contentId);
							}
						}
					}
				}
				if (!entitlementStatus) {
					if(!productId.isEmpty())
					{
						String productType = SubscriptionHelper.allProductsMap.get(productId).asObject().get("productType").asString();
						response = createHeartbeatResponse(false, productType, false, 0, 0, "Not Allowed to Playback!", false, productId);
					}
					else
					{
						response = createHeartbeatResponse(false, "", false, 0, 0, "Not Allowed to Playback!", false, "");
					}
				}
			} else {
				List<String> productIds = productDao.getProductIdsByUserId(userId);
				for (JsonValue cpProduct : cpProducts) {
					for (String product : productIds) {
						if (cpProduct.asObject().get("id").asString().equalsIgnoreCase(product)) {
							String productType = SubscriptionHelper.allProductsMap.get(product).asObject().get("productType").asString();
							if (!cpProduct.asObject().getBoolean("bundleFlag", false)) {
								response = createHeartbeatResponse(false, productType, true, 0, 0, "Allowed to Playback!", false,
										product);
								break;
							} else {
								response = createBundleHeartBeatResponse(product, userId, contentId);
							}
						}
					}
				}
			}
		} catch (BusinessApplicationException e) {
			log.error("Error, line 117 -", e);
			throw new BusinessApplicationException(e.getMsgId(), "Error from BSB");
		} catch (Exception e) {
			log.error("Error, line 120 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		response = checkLitePack(cpToken, contentId, response);
		return response;
	}

	@Override
	@Transactional
	public String addMediaToBundle(String uid, String productId, String contentId) {
		JsonObject response = new JsonObject();
		List<String> mediaList = new ArrayList<String>();
		ArrayList<String> updatableMediaList = new ArrayList<String>();
		try {
			log.info("Increment Bundle for UID:" + uid + ", Product:" + productId + ", contentId:" + contentId);
			BundleCounterVO bundleCounterVO = bundleCounterDao.getBundleCounterByUserWithProductId(productId, uid);
			if (bundleCounterVO != null) {
				long count = bundleCounterVO.getCounter();
				if (bundleCounterVO.getMediaList() != null) {
					mediaList = Arrays.asList(bundleCounterVO.getMediaList());
				}
				if (count < bundleCounterVO.getItemLimit()) {
					updatableMediaList.addAll(mediaList);
					updatableMediaList.add(contentId);
					String[] contentList = updatableMediaList.toArray(new String[updatableMediaList.size()]);
					bundleCounterVO.setMediaList(contentList);
					bundleCounterVO.setCounter(count + 1);
					response.add("count", count + 1);
					response.add("limit", bundleCounterVO.getItemLimit());
					response.add("message", "Bundle Incremented!");
					response.add("code", 200);
				}
				else
				{
					response.add("count", bundleCounterVO.getItemLimit());
					response.add("limit", bundleCounterVO.getItemLimit());
					response.add("message", "Bundle Limit Reached!");
					response.add("code", 403);
				}
				bundleCounterDao.updateBundleCounter(BundleCounterHelper.toBundleCounter(bundleCounterVO));
			}
		} catch (HttpClientErrorException e) {
			log.error("Error, line 149 -", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (Exception e) {
			log.error("Error, line 152 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return response.toString();
	}

	private String createBundleHeartBeatResponse(String productId, String userId, String contentId) {
		Boolean alreadyPlayed = checkIfPlayed(productId, contentId, userId);
		Boolean playback = (alreadyPlayed || (getBundleLimit(productId) - getBundleCount(productId, userId) > 0)) ? true
				: false;
		int bundleCounter = getBundleCount(productId, userId);
		int bundleLimit = getBundleLimit(productId);
		String message = playback ? "Playing from Bundle Pack!" : "Bundle Limit Hit!";
		String productType = SubscriptionHelper.allProductsMap.get(productId).asObject().get("productType").asString();
		return createHeartbeatResponse(true, productType, playback, bundleCounter, bundleLimit, message, alreadyPlayed, productId);
	}

	private String createHeartbeatResponse(Boolean bundle, String productType, Boolean allowPlayback, int counter, int limit,
			String message, Boolean alreadyPlayed, String productId) {
		JsonObject heartBeatResponse = new JsonObject();
		heartBeatResponse.add("entitlement", allowPlayback);
		heartBeatResponse.add("message", message);
		heartBeatResponse.add("bundleCounter", counter);
		heartBeatResponse.add("bundleFlag", bundle);
		heartBeatResponse.add("bundleLimit", limit);
		heartBeatResponse.add("productType", productType);
		heartBeatResponse.add("alreadyPlayedAsset", alreadyPlayed);
		heartBeatResponse.add("productId", productId);
		return heartBeatResponse.toString();
	}

	private int getBundleCount(String productId, String userId) {
		BundleCounterVO bundleCounterVO = bundleCounterDao.getBundleCounterByUserWithProductId(productId, userId);
		long count = 0;
		if (bundleCounterVO != null)
		{
			count = bundleCounterVO.getCounter();
		}
		return (int)count;
	}

	private int getBundleLimit(String productId) {
		JsonObject cpProduct = SubscriptionHelper.allProductsMap.get(productId);
		return cpProduct.asObject().get("bundleLimit").asInt();
	}

	private Boolean checkIfPlayed(String productId, String contentId, String userId) {
		BundleCounterVO bundleCounterVO = bundleCounterDao.getBundleCounterByUserWithProductId(productId, userId);
		if (bundleCounterVO != null && bundleCounterVO.getMediaList() != null) {
			List<String> mediaList = Arrays.asList(bundleCounterVO.getMediaList());
			for (String media : mediaList) {
				if (media.equalsIgnoreCase(contentId)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private String checkLitePack(String cpId, String contentId, String response) {
		JsonObject responseJsonObject = JsonObject.readFrom(response);
		JsonObject litePackJsonObject;
		String url = null;
		if (responseJsonObject != null && cpId.equalsIgnoreCase(WynkUtil.contentProviderSingtel)
				&& responseJsonObject.get("productType") != null && !responseJsonObject.get("productType").isNull()
				&& !responseJsonObject.get("productType").asString().isEmpty()
				&& responseJsonObject.get("productType").asString().equalsIgnoreCase("lite")) {
			url = AppgridHelper.mpxFeedData.get("programbyid").asString().replace("{0}",
					contentId + "&fields=:pack,guid");
			response = Util.executeApiGetCall(url);
			litePackJsonObject = JsonObject.readFrom(response);
			JsonArray entries = litePackJsonObject.get("entries").asArray();
			if (entries.size() > 0 && entries.get(0).asObject().get("pl1$pack") != null
					&& !entries.get(0).asObject().get("pl1$pack").isNull()
					&& entries.get(0).asObject().get("pl1$pack").asString().toLowerCase().contains("lite")) {
				responseJsonObject.set("entitlement", true);
				responseJsonObject.set("message", "Allowed to Playback!");
			}else{
				responseJsonObject.set("entitlement", false);
				responseJsonObject.set("message", "Not Allowed to Playback!");
				responseJsonObject.set("bundleFlag", false);
			}
			
		}
		return responseJsonObject.toString();
	}

}
