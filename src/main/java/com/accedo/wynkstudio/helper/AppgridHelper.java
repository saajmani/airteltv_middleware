package com.accedo.wynkstudio.helper;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.accedo.wynkstudio.exception.BusinessApplicationException;
import com.accedo.wynkstudio.util.Request;
import com.accedo.wynkstudio.util.Response;
import com.accedo.wynkstudio.util.Util;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonObject.Member;

public class AppgridHelper {
	
	public static String authenticatingMpxBaseUrl = null;
	public static String appGridSessionkey = null;
	public static JsonObject appGridMetadata = null;
	public static JsonObject appGridAssets = null;
	public static JsonObject mpxFeedData = null;
	public static JsonObject appGridRailConfiguration = null;
	public static JsonObject appGridCardConfiguration = null;
	public static String mpxAccountId = "";
	public static String trustedDirectoryPid = "";
	public static Properties prop = null;
	public static String[] contentProviders = null;
	public static JsonObject bsbProvisioningProductMap = null;
	public static String defaultsorting = null;
	public static JsonObject filterCategories = null;
	
	
	public static HashMap<String, HashMap<String, String>> assetsMap = null;
	public static HashMap<String, HashMap<String, String>> newAssetsMap = null;
	public static HashMap<String, String> iosAssetsMap = null;
	public static HashMap<String, String> newIosAssetsMap = null;
	public static byte[] assetsZip = null;
	public static byte[] newAssetsZip = null;
	public static byte[] assetsZipMdpi = null;
	public static byte[] assetsZipHdpi = null;
	public static byte[] assetsZipXhdpi = null;
	public static byte[] assetsZipXxhdpi = null;
	public static byte[] newAssetsZipMdpi = null;
	public static byte[] newAssetsZipHdpi = null;
	public static byte[] newAssetsZipXhdpi = null;
	public static byte[] newAssetsZipXxhdpi = null;
	public static byte[] assetsIosIphone = null;
	public static byte[] assetsIosIpad = null;
	public static byte[] newAssetsIosIphone = null;
	public static byte[] newAssetsIosIpad = null;
	public static HashMap<String, String> filterMap = null;
	
	/* Get AppGrid Session Key */
	public static String getSession() {
		String response = "";
		try {
			prop = Util.getPropValues();
			String baseURL = prop.getProperty("wynkstudio.appgrid.api.baseurl");
			String appKey = (System.getProperty("APPGRID_KEY") != null && !System.getProperty("APPGRID_KEY").isEmpty()) ? System
					.getProperty("APPGRID_KEY") : prop.getProperty("wynkstudio.appgrid.session.appKey");
			String url = prop.getProperty("wynkstudio.appgrid.api.session");
			url = url.replace("{0}", appKey).replace("{1}", prop.getProperty("wynkstudio.appgrid.session.uuid"));
			response = baseURL + url;
			response = Util.executeApiGetCall(response);
			JsonObject jsonObject = JsonObject.readFrom(response);
			response = jsonObject.get("sessionKey").asString();
		} catch (HttpClientErrorException e) {
			throw new BusinessApplicationException(e.getStatusCode().value(), e, e.getStatusText());
		} catch (IOException e) {
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e, e.getMessage());
		}
		return response;
	}
	
	/* Get AppGrid Session Key */
	public static String getSession(HttpHeaders headers, String appKey) {
		String response = "";
		try {
			prop = Util.getPropValues();
			String baseURL = prop.getProperty("wynkstudio.appgrid.api.baseurl");
			String applicationKey = appKey;
			String url = prop.getProperty("wynkstudio.appgrid.api.session");
			url = url.replace("{0}", applicationKey).replace("{1}", prop.getProperty("wynkstudio.appgrid.session.uuid"));
			response = baseURL + url;
			response = Util.executeApiGetCall(response);
			JsonObject jsonObject = JsonObject.readFrom(response);
			response = jsonObject.get("sessionKey").asString();
		} catch (HttpClientErrorException e) {
			throw new BusinessApplicationException(e.getStatusCode().value(), e, e.getStatusText());
		} catch (IOException e) {
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e, e.getMessage());
		}
		return response;
	}
	
	public static JsonObject updateMetadata(HttpHeaders headers)
	{
		String response = "";
		String baseURL = prop.getProperty("wynkstudio.appgrid.api.baseurl");
		String url = "/metadata";
		try {
			headers.set("X-Session", appGridSessionkey);
			url = baseURL + url;
			response = Util.executeApiGetCall(url, headers);
			appGridMetadata = JsonObject.readFrom(response);
			mpxFeedData = appGridMetadata.get("cp_feeds_2.0").asObject();
			authenticatingMpxBaseUrl = appGridMetadata.get("mpx_SignIn_Url").asString();
			mpxAccountId = appGridMetadata.get("mpx_account_id").asString();
			trustedDirectoryPid = appGridMetadata.get("mpx_trusted_pid").asString();
			contentProviders = appGridMetadata.get("available_cps").asString().replace("[", "").replace("]", "").split(",");
			appGridRailConfiguration = appGridMetadata.get("product_rails").asObject();
			appGridCardConfiguration = appGridMetadata.get("cards").asObject();
			bsbProvisioningProductMap = appGridMetadata.get("bsb_provision_product_map").asObject();
			defaultsorting = appGridMetadata.get("default_sorting_in_filter").asString();
			SubscriptionHelper.getProducts(headers);
			setFilterMapping();
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode().value() == HttpStatus.UNAUTHORIZED.value()) {
				appGridSessionkey = getSession();
				return updateMetadata(headers);
			}
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		}
		return appGridMetadata;
	}
	
	public static JsonObject updateAssets(HttpHeaders headers,String appKey, String sessionKey)
	{
		String response = "";
		String baseURL = prop.getProperty("wynkstudio.appgrid.api.baseurl");
		String url = "/asset";
		try {
			headers.set("X-Session", sessionKey);
			url = baseURL + url;
			response = Util.executeApiGetCall(url, headers);
			appGridAssets= JsonObject.readFrom(response);	
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode().value() == HttpStatus.UNAUTHORIZED.value()) {
				sessionKey = getSession(headers,appKey);
				return updateAssets(headers, appKey, sessionKey);
			}
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		}
		return appGridAssets;
	}
	public static String getAllAssets(HttpHeaders headers)
	{
		String response = "";
		String baseURL = prop.getProperty("wynkstudio.appgrid.api.baseurl");
		String url = "/asset";
		try {
			headers.set("X-Session", appGridSessionkey);
			url = baseURL + url;
			response = Util.executeApiGetCall(url, headers);
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode().value() == HttpStatus.UNAUTHORIZED.value()) {
				appGridSessionkey = getSession();
				return getAllAssets(headers);
			}
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		}
		return response;
	}

//	/* Support method to get or post data to/from AppGrid */
//	public static String getOrUpdateAppgridData(String userId, String requestBody, String dataKey, HttpHeaders headers) {
//		String response = "";
//		String baseURL =  prop.getProperty("wynkstudio.appgrid.api.baseurl");
//		String url = prop.getProperty("wynkstudio.appgrid.api.group.user.bykey");
//		url = url.replace("{0}", userId).replace("{1}", dataKey);
//		try {
//			headers.set("X-Session", appGridSessionkey);
//			response = baseURL + url;
//			if (requestBody != null) {
//				response = Util.executeApiPostCall(response, headers, requestBody);
//			} else {
//				response = Util.executeApiGetCall(response, headers);
//			}
//		} catch (HttpClientErrorException e) {
//			if (e.getStatusCode().value() == HttpStatus.UNAUTHORIZED.value()) {
//				appGridSessionkey = getSession();
//				return getOrUpdateAppgridData(userId, requestBody, dataKey, headers);
//			}
//			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
//		} catch (Exception e) {
//			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e, e.getMessage());
//		}
//		return response;
//	}
	
	/* Support method to get or post data to/from AppGrid */
	public static String getOrUpdateUserProfile(String userId, String requestBody, HttpHeaders headers) {
		String response = "";
		String baseURL =  prop.getProperty("wynkstudio.appgrid.api.baseurl");
		String url = prop.getProperty("wynkstudio.appgrid.api.group.user");
		url = url.replace("{0}", userId);
		try {
			headers.set("X-Session", appGridSessionkey);
			response = baseURL + url;
			if (requestBody != null) {
				response = Util.executeApiPostCall(response, headers, requestBody);
			} else {
				response = Util.executeApiGetCall(response, headers);
			}
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode().value() == HttpStatus.UNAUTHORIZED.value()) {
				appGridSessionkey = getSession();
				return getOrUpdateUserProfile(userId, requestBody, headers);
			}
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (Exception e) {
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e, e.getMessage());
		}
		return response;
	}
	
	/* Support method to add Product AppGrid UserInfo */
	
//	/* Support method to delete Product from AppGrid UserInfo */
//	public static void deleteProductFromAppgrid(String userId, String product, HttpHeaders headers)
//	{
//		String userProductField = prop.getProperty("wynkstudio.apprid.user.products");
//		String entitlements = getOrUpdateAppgridData(userId, null, userProductField, headers);
//		Boolean productFlag = false;
//		int index = 0;
//		JsonArray productObject = JsonArray.readFrom(entitlements);		
//		for (int i = 0; i < productObject.size(); i++) {
//			if ((productObject.get(i).asObject().get("productId").asString().equalsIgnoreCase(product))) {
//				productFlag = true;
//				index = i;
//			}
//		}
//		if (productFlag) {
//			productObject.remove(index);
//		}
//		String finalJson = productObject.toString();
//		getOrUpdateAppgridData(userId, finalJson, userProductField, headers);
//	}
	
	public static HashMap<String, HashMap<String, String>> getAssetsFromAppgridAndroid(HttpHeaders headers) {
		HashMap<String, HashMap<String, String>> assetsMap = new HashMap<String, HashMap<String, String>>();
		String androidAppKey = appGridMetadata.get("android_app_key").asString();
		String sessionKey = AppgridHelper.getSession(headers, androidAppKey);
		JsonObject appgridAssets = AppgridHelper.updateAssets(headers, androidAppKey, sessionKey);
		Iterator<Member> i = appgridAssets.iterator();
		String assetUrl = null;
		String key = null;
		String type = null;
		HashMap<String, String> tempAssetsMap = null;
		JsonObject androidRefreshAssets = JsonObject.readFrom(appGridMetadata.get("android_refresh_images").asString());
//				(JsonObject) appGridMetadata.get("android_refresh_images");
		while (i.hasNext()) {
			key = i.next().getName();
			if ((key.toLowerCase().contains("subscription_image_")) || (key.toLowerCase().contains("asset_img") && key.toLowerCase().contains("dpi") && !key.toLowerCase().contains("_tour_"))) {
				assetUrl = androidRefreshAssets.get(key) != null ? androidRefreshAssets.get(key).asString() : appgridAssets.get(key).asString();
				type = key.substring(key.lastIndexOf("_") + 1).toLowerCase();
				tempAssetsMap = assetsMap.get(type) != null ? assetsMap.get(type)
						: new HashMap<String, String>();
				tempAssetsMap.put(key, assetUrl);
				assetsMap.put(type, tempAssetsMap);
			}
		}
		
		AppgridHelper.assetsMap = assetsMap;
		return assetsMap;
	}
	
	public static HashMap<String, HashMap<String, String>> getAssetsFromAppgridAndroidForNewVersion(HttpHeaders headers) {
		HashMap<String, HashMap<String, String>> assetsMap = new HashMap<String, HashMap<String, String>>();
		
		
		
		String androidAppKey = appGridMetadata.get("new_android_app_key").asString();
		
		
		
		String sessionKey = AppgridHelper.getSession(headers, androidAppKey);
		JsonObject appgridAssets = AppgridHelper.updateAssets(headers, androidAppKey, sessionKey);
		Iterator<Member> i = appgridAssets.iterator();
		String assetUrl = null;
		String key = null;
		String type = null;
		HashMap<String, String> tempAssetsMap = null;
		JsonObject androidRefreshAssets = JsonObject.readFrom(appGridMetadata.get("android_refresh_images").asString());
//				(JsonObject) appGridMetadata.get("android_refresh_images");
		while (i.hasNext()) {
			key = i.next().getName();
			if ((key.toLowerCase().contains("subscription_image_")) || (key.toLowerCase().contains("asset_img") && key.toLowerCase().contains("dpi") && !key.toLowerCase().contains("_tour_"))) {
				assetUrl = androidRefreshAssets.get(key) != null ? androidRefreshAssets.get(key).asString() : appgridAssets.get(key).asString();
				type = key.substring(key.lastIndexOf("_") + 1).toLowerCase();
				tempAssetsMap = assetsMap.get(type) != null ? assetsMap.get(type)
						: new HashMap<String, String>();
				tempAssetsMap.put(key, assetUrl);
				assetsMap.put(type, tempAssetsMap);
			}
		}
		
		AppgridHelper.newAssetsMap = assetsMap;
		return assetsMap;
	}
	
	
	public static HashMap<String, String> getAssetsFromAppgridIos(HttpHeaders headers) {
		HashMap<String, String> iosAssetsMap = new HashMap<String, String>();
		String iosAppKey = appGridMetadata.get("ios_app_key").asString();
		String sessionKey = AppgridHelper.getSession(headers, iosAppKey);
		JsonObject appgridAssets = AppgridHelper.updateAssets(headers, iosAppKey, sessionKey);
		Iterator<Member> i = appgridAssets.iterator();
		String assetUrl = null;
		String key = null;
		while (i.hasNext()) {
			key = i.next().getName();
			if (!key.toLowerCase().contains("dpi") && !key.toLowerCase().contains("libs") && !key.toLowerCase().contains("subscibtion_wynk_logo")) {
				assetUrl = appgridAssets.get(key).asString();
				iosAssetsMap.put(key, assetUrl);
				
			}
		}
		AppgridHelper.iosAssetsMap = iosAssetsMap;
		JsonObject iosRefreshAssets = JsonObject.readFrom(appGridMetadata.get("ios_refresh_images").asString());
		Iterator<Member> iosKeys = iosRefreshAssets.iterator();
		while (iosKeys.hasNext()) {
			key = iosKeys.next().getName();
			AppgridHelper.iosAssetsMap.put(key, iosRefreshAssets.get(key).asString());
		}
				
		
	/*	AppgridHelper.iosAssetsMap.put("asset_img_dm_logo", "http://appgrid-api.cloud.accedo.tv/asset/5564d6d46cea3f5c9e345bf0745a0a3e2ca529f93cea4970");
		AppgridHelper.iosAssetsMap.put("asset_img_eros_now_logo", "http://appgrid-api.cloud.accedo.tv/asset/5564d6d46cea3fdc9e345bf0745a0a3e2ca529f93cea49a0");
		AppgridHelper.iosAssetsMap.put("asset_img_hooq_logo", "http://appgrid-api.cloud.accedo.tv/asset/5564d6d46cea4f5c9e345bf0745a0a3e2ca529f93cea49d0");
		AppgridHelper.iosAssetsMap.put("asset_img_youtube_sidemenu", "http://appgrid-api.cloud.accedo.tv/asset/5564d6d46cea2fdc9e345bf0745a0a3e2ca529f93cea4940");
		AppgridHelper.iosAssetsMap.put("asset_img_sony_liv_logo", "http://appgrid-api.cloud.accedo.tv/asset/5564d6d46cea4fcc9e345bf0745a0a3e2ca529f93cea5900");
		AppgridHelper.iosAssetsMap.put("asset_img_wynk_studio", "http://appgrid-api.cloud.accedo.tv/asset/5564d6d46cea5f8c9e345bf0745a0a3e2ca529f93cea5940");
		
		AppgridHelper.iosAssetsMap.put("asset_img_sony_liv_logo_long", "http://appgrid-api.cloud.accedo.tv/asset/55657a03077c0523ee44bb00c2253e07e8a025b37185de8a");
		AppgridHelper.iosAssetsMap.put("asset_img_dm_logo_long", "http://appgrid-api.cloud.accedo.tv/asset/55657a03076ca5c3ee44bb00c2253e07e8a025b37185ceda");
		AppgridHelper.iosAssetsMap.put("asset_img_eros_now_logo_long", "http://appgrid-api.cloud.accedo.tv/asset/5565ba93c7ac1553ee44bb0072959e170870f52381a5feea");
*/		return iosAssetsMap;
	}
	
	public static HashMap<String, String> getAssetsFromAppgridIosForNewVersion(HttpHeaders headers) {
		HashMap<String, String> iosAssetsMap = new HashMap<String, String>();
		
		
		String iosAppKey = appGridMetadata.get("new_ios_app_key").asString();
		
		
		String sessionKey = AppgridHelper.getSession(headers, iosAppKey);
		JsonObject appgridAssets = AppgridHelper.updateAssets(headers, iosAppKey, sessionKey);
		Iterator<Member> i = appgridAssets.iterator();
		String assetUrl = null;
		String key = null;
		while (i.hasNext()) {
			key = i.next().getName();
			if (!key.toLowerCase().contains("dpi") && !key.toLowerCase().contains("libs") && !key.toLowerCase().contains("subscibtion_wynk_logo")) {
				assetUrl = appgridAssets.get(key).asString();
				iosAssetsMap.put(key, assetUrl);
				
			}
		}
		AppgridHelper.newIosAssetsMap = iosAssetsMap;
		JsonObject iosRefreshAssets = JsonObject.readFrom(appGridMetadata.get("ios_refresh_images").asString());
		Iterator<Member> iosKeys = iosRefreshAssets.iterator();
		while (iosKeys.hasNext()) {
			key = iosKeys.next().getName();
			AppgridHelper.newIosAssetsMap.put(key, iosRefreshAssets.get(key).asString());
		}
				
		
	/*	AppgridHelper.iosAssetsMap.put("asset_img_dm_logo", "http://appgrid-api.cloud.accedo.tv/asset/5564d6d46cea3f5c9e345bf0745a0a3e2ca529f93cea4970");
		AppgridHelper.iosAssetsMap.put("asset_img_eros_now_logo", "http://appgrid-api.cloud.accedo.tv/asset/5564d6d46cea3fdc9e345bf0745a0a3e2ca529f93cea49a0");
		AppgridHelper.iosAssetsMap.put("asset_img_hooq_logo", "http://appgrid-api.cloud.accedo.tv/asset/5564d6d46cea4f5c9e345bf0745a0a3e2ca529f93cea49d0");
		AppgridHelper.iosAssetsMap.put("asset_img_youtube_sidemenu", "http://appgrid-api.cloud.accedo.tv/asset/5564d6d46cea2fdc9e345bf0745a0a3e2ca529f93cea4940");
		AppgridHelper.iosAssetsMap.put("asset_img_sony_liv_logo", "http://appgrid-api.cloud.accedo.tv/asset/5564d6d46cea4fcc9e345bf0745a0a3e2ca529f93cea5900");
		AppgridHelper.iosAssetsMap.put("asset_img_wynk_studio", "http://appgrid-api.cloud.accedo.tv/asset/5564d6d46cea5f8c9e345bf0745a0a3e2ca529f93cea5940");
		
		AppgridHelper.iosAssetsMap.put("asset_img_sony_liv_logo_long", "http://appgrid-api.cloud.accedo.tv/asset/55657a03077c0523ee44bb00c2253e07e8a025b37185de8a");
		AppgridHelper.iosAssetsMap.put("asset_img_dm_logo_long", "http://appgrid-api.cloud.accedo.tv/asset/55657a03076ca5c3ee44bb00c2253e07e8a025b37185ceda");
		AppgridHelper.iosAssetsMap.put("asset_img_eros_now_logo_long", "http://appgrid-api.cloud.accedo.tv/asset/5565ba93c7ac1553ee44bb0072959e170870f52381a5feea");
*/		return iosAssetsMap;
	}
	
	@SuppressWarnings("rawtypes")
	public static byte[] getZipOfAppgridIosAssets(String dpi, HttpHeaders headers) {
		// Creating byteArray stream, make it buffer-able and passing this
		// buffer to ZipOutputStream
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
		ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);
		String key = null;
		String deviceType = dpi.toLowerCase().contains("iphone") ? "ipad" : "iphone";
		try {;
			AppgridHelper.iosAssetsMap = AppgridHelper.iosAssetsMap != null ? AppgridHelper.iosAssetsMap :  getAssetsFromAppgridIos(headers) ;  
			// Packing Files
			Iterator<?> it = AppgridHelper.iosAssetsMap.entrySet().iterator();
			List<String> kList = new ArrayList<>();
			ExecutorService es = Executors.newFixedThreadPool(2);
			String extension = "";
			List<Future<Response>> futures = new ArrayList<Future<Response>>();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				key = pair.getKey().toString();
				if (!key.toLowerCase().contains(deviceType)) {
					final String url = pair.getValue().toString();
					extension = key.trim().equalsIgnoreCase("privacy_policy") || key.trim().equalsIgnoreCase("terms_and_conditions_html") ? ".html" : key.trim().equalsIgnoreCase("adobe_agent_config_file") ? ".json" : ".png";
						kList.add(key.trim() + extension);
					futures.add(es.submit(new Request(new URL(url))));
				}
			}
			int k = 0;
			for (Future<Response> future : futures) {
				InputStream body = future.get().getBody();
				zipOutputStream.putNextEntry(new ZipEntry(kList.get(k)));
				IOUtils.copy(body, zipOutputStream);
				zipOutputStream.closeEntry();
				k++;
			}
			if (zipOutputStream != null) {
				zipOutputStream.finish();
				zipOutputStream.flush();
				IOUtils.closeQuietly(zipOutputStream);
			}
			IOUtils.closeQuietly(bufferedOutputStream);
			IOUtils.closeQuietly(byteArrayOutputStream);
			es.shutdown();
		} catch (IOException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		AppgridHelper.assetsZip = byteArrayOutputStream.toByteArray();
		return AppgridHelper.assetsZip;
	}
	
	@SuppressWarnings("rawtypes")
	public static byte[] getZipOfAppgridIosAssetsForNewVersion(String dpi, HttpHeaders headers) {
		// Creating byteArray stream, make it buffer-able and passing this
		// buffer to ZipOutputStream
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
		ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);
		String key = null;
		String deviceType = dpi.toLowerCase().contains("iphone") ? "ipad" : "iphone";
		try {;
			AppgridHelper.newIosAssetsMap = AppgridHelper.newIosAssetsMap != null ? AppgridHelper.newIosAssetsMap :  getAssetsFromAppgridIosForNewVersion(headers) ;  
			// Packing Files
			Iterator<?> it = AppgridHelper.newIosAssetsMap.entrySet().iterator();
			List<String> kList = new ArrayList<>();
			ExecutorService es = Executors.newFixedThreadPool(2);
			String extension = "";
			List<Future<Response>> futures = new ArrayList<Future<Response>>();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				key = pair.getKey().toString();
				if (!key.toLowerCase().contains(deviceType)) {
					final String url = pair.getValue().toString();
					extension = key.trim().equalsIgnoreCase("privacy_policy") || key.trim().equalsIgnoreCase("terms_and_conditions_html") ? ".html" : key.trim().equalsIgnoreCase("adobe_agent_config_file") ? ".json" : ".png";
						kList.add(key.trim() + extension);
					futures.add(es.submit(new Request(new URL(url))));
				}
			}
			int k = 0;
			for (Future<Response> future : futures) {
				InputStream body = future.get().getBody();
				zipOutputStream.putNextEntry(new ZipEntry(kList.get(k)));
				IOUtils.copy(body, zipOutputStream);
				zipOutputStream.closeEntry();
				k++;
			}
			if (zipOutputStream != null) {
				zipOutputStream.finish();
				zipOutputStream.flush();
				IOUtils.closeQuietly(zipOutputStream);
			}
			IOUtils.closeQuietly(bufferedOutputStream);
			IOUtils.closeQuietly(byteArrayOutputStream);
			es.shutdown();
		} catch (IOException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		AppgridHelper.newAssetsZip = byteArrayOutputStream.toByteArray();
		return AppgridHelper.newAssetsZip;
	}
	
	public static byte[] getZipOfAppgridAssets(String dpi) {
		// Creating byteArray stream, make it buffer-able and passing this
		// buffer to ZipOutputStream
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
		ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);
		try {
			// Packing Files
			Iterator<?> it = AppgridHelper.assetsMap.get(dpi).entrySet().iterator();
			List<String> kList = new ArrayList<>();
			ExecutorService es = Executors.newFixedThreadPool(2);
			List<Future<Response>> futures = new ArrayList<Future<Response>>();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				final String url = pair.getValue().toString();
				kList.add(pair.getKey().toString().trim() + ".png");
				futures.add(es.submit(new Request(new URL(url))));
			}
			int k = 0;
			for (Future<Response> future : futures) {
				InputStream body = future.get().getBody();
				zipOutputStream.putNextEntry(new ZipEntry(kList.get(k)));
				IOUtils.copy(body, zipOutputStream);
				zipOutputStream.closeEntry();
				k++;
			}
			if (zipOutputStream != null) {
				zipOutputStream.finish();
				zipOutputStream.flush();
				IOUtils.closeQuietly(zipOutputStream);
			}
			IOUtils.closeQuietly(bufferedOutputStream);
			IOUtils.closeQuietly(byteArrayOutputStream);
			es.shutdown();
		} catch (IOException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		AppgridHelper.assetsZip = byteArrayOutputStream.toByteArray();
		return AppgridHelper.assetsZip;
	}
	
	public static byte[] getZipOfAppgridAssetsForNewVersion(String dpi) {
		// Creating byteArray stream, make it buffer-able and passing this
		// buffer to ZipOutputStream
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
		ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);
		try {
			// Packing Files
			Iterator<?> it = AppgridHelper.newAssetsMap.get(dpi).entrySet().iterator();
			List<String> kList = new ArrayList<>();
			ExecutorService es = Executors.newFixedThreadPool(2);
			List<Future<Response>> futures = new ArrayList<Future<Response>>();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				final String url = pair.getValue().toString();
				kList.add(pair.getKey().toString().trim() + ".png");
				futures.add(es.submit(new Request(new URL(url))));
			}
			int k = 0;
			for (Future<Response> future : futures) {
				InputStream body = future.get().getBody();
				zipOutputStream.putNextEntry(new ZipEntry(kList.get(k)));
				IOUtils.copy(body, zipOutputStream);
				zipOutputStream.closeEntry();
				k++;
			}
			if (zipOutputStream != null) {
				zipOutputStream.finish();
				zipOutputStream.flush();
				IOUtils.closeQuietly(zipOutputStream);
			}
			IOUtils.closeQuietly(bufferedOutputStream);
			IOUtils.closeQuietly(byteArrayOutputStream);
			es.shutdown();
		} catch (IOException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		AppgridHelper.newAssetsZip = byteArrayOutputStream.toByteArray();
		return AppgridHelper.newAssetsZip;
	}
	
	public static void updateAppgridAssets(HttpHeaders headers)
	{
		getAssetsFromAppgridAndroid(headers);
		AppgridHelper.assetsZipHdpi = getZipOfAppgridAssets("hdpi");
		AppgridHelper.assetsZipMdpi = getZipOfAppgridAssets("mdpi");
		AppgridHelper.assetsZipXhdpi = getZipOfAppgridAssets("xhdpi") ;
		AppgridHelper.assetsZipXxhdpi = getZipOfAppgridAssets("xxhdpi");
	}
	
	public static void updateAppgridAssetsForNewVersion(HttpHeaders headers)
	{
		getAssetsFromAppgridAndroidForNewVersion(headers);
		AppgridHelper.newAssetsZipHdpi = getZipOfAppgridAssetsForNewVersion("hdpi");
		AppgridHelper.newAssetsZipMdpi= getZipOfAppgridAssetsForNewVersion("mdpi");
		AppgridHelper.newAssetsZipXhdpi = getZipOfAppgridAssetsForNewVersion("xhdpi") ;
		AppgridHelper.newAssetsZipXxhdpi = getZipOfAppgridAssetsForNewVersion("xxhdpi");
	}
	
	public static void updateAppgridAssetsIos(HttpHeaders headers)
	{
		getAssetsFromAppgridIos(headers);
		AppgridHelper.assetsIosIpad = getZipOfAppgridIosAssets("ipad", headers);
		AppgridHelper.assetsIosIphone = getZipOfAppgridIosAssets("iphone", headers);
	}
	
	public static void updateAppgridAssetsIosForNewVersion(HttpHeaders headers)
	{
		getAssetsFromAppgridIosForNewVersion(headers);
		AppgridHelper.newAssetsIosIpad = getZipOfAppgridIosAssetsForNewVersion("ipad", headers);
		AppgridHelper.newAssetsIosIphone = getZipOfAppgridIosAssetsForNewVersion("iphone", headers);
	}
	
	
	public static void setFilterMapping() {
		filterMap = new HashMap<>();
		JsonArray asArray = null;
		filterCategories  = new JsonObject();
		List<String> names = AppgridHelper.appGridMetadata.get("filter").asObject().names();
		for (int i = 0; i < names.size(); i++) {
			JsonArray filterJsonArray =  AppgridHelper.appGridMetadata.get("filter").asObject().get(names.get(i)).asArray();
			JsonObject jsonObject = null;
			for (int j = 0; j < filterJsonArray.size(); j++) {
				jsonObject = filterJsonArray.get(j).asObject();
				if(!jsonObject.get("label").asString().equalsIgnoreCase("Sort By") && !jsonObject.get("label").asString().equalsIgnoreCase("Language")){
					filterCategories.set(jsonObject.get("label").asString(), jsonObject.get("label").asString());
					asArray = jsonObject.get("category").asArray();
					for (int k = 0; k < asArray.size(); k++) {
						filterMap.put(asArray.get(k).asObject().get("tag").asString(), jsonObject.get("label").asString());
					}
				}
				
			}
		}
	}
}
