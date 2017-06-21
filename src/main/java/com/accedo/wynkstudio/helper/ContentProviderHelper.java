package com.accedo.wynkstudio.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.simple.JSONArray;

import java.util.Map.Entry;

import com.accedo.wynkstudio.util.Util;
import com.accedo.wynkstudio.util.WynkUtil;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

/**
 * @author Accedo Software Private Limited
 * @version 1.0
 * @since 2014-07-01
 */

public class ContentProviderHelper {

	@SuppressWarnings("unchecked")
	public static String createJsonResponse(String response) {
		Map<String, JsonArray> creditsHashMap = new HashMap<String, JsonArray>();
		JsonArray contentProvidersJsonArray = null;
		JsonArray tagsJsonArray = null;
		JsonArray trailerUrlJsonArray = null;
		JsonArray packJsonArray = null;
		JsonObject tagsJsonObject = null;
		JsonObject cpTagsJsonObject = null;
		JsonArray mediaJsonArray = new JsonArray();
		JSONArray erosNowJsonArray = new JSONArray();
		JSONArray singtelJsonArray = new JSONArray();
		JSONArray sonyLivJsonArray = new JSONArray();
		JSONArray dailymotionJsonArray = new JSONArray();
		JSONArray youtubeJsonArray = new JSONArray();
		JsonObject mediaJsonObject = null;
		String provider = null;
		String[] creditEntries = WynkUtil.WYNKSTUDIO_JSON_FIELD_CREDITTYPE_ENTRIES.split("_");
		String[] allCreditTypes = WynkUtil.WYNKSTUDIO_JSON_FIELD_CREDITTYPE_ALL.split("_");
		for (int i = 0; i < allCreditTypes.length; i++) {
			creditsHashMap.put(allCreditTypes[i].toLowerCase(), new JsonArray());
		}

		JsonObject movieJsonObject = JsonObject.readFrom(response);
		String   cpSorting = movieJsonObject.get("cp_sorting") != null && !movieJsonObject.get("cp_sorting").isNull() ? movieJsonObject.get("cp_sorting").asString() : "";
		// JsonObject convertedJsonObject =
		// JsonObject.readFrom(convertedResponse);
		JsonArray entries = movieJsonObject.get(WynkUtil.WYNKSTUDIO_JSON_FIELD_ENTRIES).asArray();
		// JsonArray convertedJsonEntries =
		// convertedJsonObject.get(entry).asArray();
		for (int j = 0; j < entries.size(); j++) {
			JsonObject jsonContent = entries.get(j).asObject();
			JsonObject convertedJsonContent = convertMpxJsonToSpecFormat(jsonContent);
			// convertedJsonEntries.get(j).asObject();
			JsonArray languageArray = (convertedJsonContent.get("languages") != null && !convertedJsonContent.get("languages").isNull()) ? convertedJsonContent.get("languages").asArray() :  new JsonArray();
			for (int i = 0; i < languageArray.size(); i++) {
				String language = languageArray.get(0).asString();
				JsonObject languageObject = JsonObject.readFrom(AppgridHelper.appGridMetadata.get("language_full_form")
						.asString());
				String languageFullForm = (languageObject.get(language) != null && !languageObject.get(language)
						.isNull()) ? languageObject.get(language).asString() : language;
				languageArray.set(0, languageFullForm);
			}
			convertedJsonContent.set("languages", languageArray);
			if (convertedJsonContent.get("programType") != null && !convertedJsonContent.get("programType").isNull()
					&& !convertedJsonContent.get("programType").asString().equalsIgnoreCase("series")) {
				convertedJsonContent = appendMediaThumbnailImageUrlToImages(jsonContent, convertedJsonContent);
			} else {
				if (convertedJsonContent.get("programType") != null
						&& !convertedJsonContent.get("programType").isNull()
						&& convertedJsonContent.get("programType").asString().equalsIgnoreCase("series")) {
					int index = convertedJsonContent.get("guid").asString().lastIndexOf("/");
					String seriesId = convertedJsonContent.get("guid").asString().substring(index + 1);
					convertedJsonContent.set("guid", seriesId);
					convertedJsonContent.set(
							"images",
							convertedJsonContent.get("thumbnails") != null
									&& !convertedJsonContent.get("thumbnails").isNull() ? convertedJsonContent
									.get("thumbnails") : null);
				}
			}

			if (jsonContent.get("programType") != null && !jsonContent.get("programType").isNull()) {
				mediaJsonArray = new JsonArray();
				mediaJsonObject = new JsonObject();
				if (jsonContent.get("media").asArray().size() > 0) {
					mediaJsonObject.add("url", jsonContent.get("media").asArray().get(0).asObject().get("content")
							.asArray().get(0).asObject().get("streamingUrl").asString());
					mediaJsonObject.add("releaseUrl",
							jsonContent.get("media").asArray().get(0).asObject().get("content").asArray().get(0)
									.asObject().get("releases").asArray().get(0).asObject().get("url").asString());
					mediaJsonArray.add(mediaJsonObject);
				}
				convertedJsonContent.set("media", mediaJsonArray);
			}

			convertedJsonContent.set("releaseDate",
					Util.convertDateToSeconds((convertedJsonContent.get("releaseDate") != null && !convertedJsonContent
							.get("releaseDate").isNull()) ? convertedJsonContent.get("releaseDate").asString() : ""));

			JsonArray creditsJsonArray = convertedJsonContent.get(WynkUtil.WYNKSTUDIO_JSON_FIELD_CREDITS).asArray();

			if (convertedJsonContent.get("programType") != null && !convertedJsonContent.get("programType").isNull()
					&& convertedJsonContent.get("programType").asString().equalsIgnoreCase("other")) {
				convertedJsonContent.set("programType", "video");
			}

			JsonObject creditTypeJsonObject = null;
			for (int i = 0; i < creditsJsonArray.size(); i++) {
				JsonObject creditJsonObject = creditsJsonArray.get(i).asObject();
				if (creditJsonObject.get(WynkUtil.WYNKSTUDIO_JSON_FIELD_CREDITTYPE) != null && !creditJsonObject.get(WynkUtil.WYNKSTUDIO_JSON_FIELD_CREDITTYPE).isNull()) {
					String roleType = creditJsonObject.get(WynkUtil.WYNKSTUDIO_JSON_FIELD_CREDITTYPE).asString().toLowerCase();
					if (roleType.equals("cast")) {
						roleType = "actor";
					}
					JsonArray creditJsonArray = creditsHashMap.get(roleType);

					creditTypeJsonObject = new JsonObject();
					creditTypeJsonObject.add(WynkUtil.WYNKSTUDIO_JSON_FIELD_PERSONNAME, (creditJsonObject.get(WynkUtil.WYNKSTUDIO_JSON_FIELD_PERSONNAME) != null && !creditJsonObject
							.get(WynkUtil.WYNKSTUDIO_JSON_FIELD_PERSONNAME).isNull()) ? creditJsonObject.get(WynkUtil.WYNKSTUDIO_JSON_FIELD_PERSONNAME).asString() : "");
					creditTypeJsonObject.add(WynkUtil.WYNKSTUDIO_JSON_FIELD_CHARACTERNAME,
							(creditJsonObject.get(WynkUtil.WYNKSTUDIO_JSON_FIELD_CHARACTERNAME) != null && !creditJsonObject.get(WynkUtil.WYNKSTUDIO_JSON_FIELD_CHARACTERNAME)
									.isNull()) ? creditJsonObject.get(WynkUtil.WYNKSTUDIO_JSON_FIELD_CHARACTERNAME).asString() : "");
					creditTypeJsonObject.add(WynkUtil.WYNKSTUDIO_JSON_FIELD_CREDITTYPE, creditJsonObject.get(WynkUtil.WYNKSTUDIO_JSON_FIELD_CREDITTYPE).asString().toLowerCase()
							.equals("cast") ? "Actor" : creditJsonObject.get(WynkUtil.WYNKSTUDIO_JSON_FIELD_CREDITTYPE).asString());
					if (creditJsonArray != null) {
						creditJsonArray.add(creditTypeJsonObject);
					} else {
						creditJsonArray = new JsonArray();
						creditJsonArray.add(creditTypeJsonObject);
					}

					creditsHashMap.put(roleType, creditJsonArray);

				}
			}
			if (creditsHashMap.keySet().size() == creditEntries.length) {
				int k = 0;
				creditTypeJsonObject = new JsonObject();
				/*
				 * Here actor act as an cast so it will be first after sorting
				 */
				SortedSet<String> keys = new TreeSet<String>(creditsHashMap.keySet());
				Arrays.sort(creditEntries);

				for (String key : keys) {
					if (creditsHashMap.get(key) != null && creditsHashMap.get(key).size() > 0) {
						creditTypeJsonObject.add(creditEntries[k], creditsHashMap.get(key));
						creditsHashMap.put(key, new JsonArray());
					}
					k++;
				}
			}
			convertedJsonContent.set(WynkUtil.WYNKSTUDIO_JSON_FIELD_CREDITS,
					creditTypeJsonObject != null && !creditTypeJsonObject.isNull() ? creditTypeJsonObject
							: new JsonObject());

			if (convertedJsonContent.get("programType") == null) {
				convertedJsonContent.set("programType", "people");
				convertedJsonContent = appendThumbnailImageUrlToImages(convertedJsonContent);
			}

			if (convertedJsonContent.get("description") == null || convertedJsonContent.get("description").isNull()
					|| convertedJsonContent.get("description").asString().isEmpty()) {
				if (jsonContent.get("shortDescription") != null && !jsonContent.get("shortDescription").isNull()) {
					convertedJsonContent.set("description", jsonContent.get("shortDescription").asString());
				}
			}

			if (convertedJsonContent.get("id").asString().contains("SINGTEL")) {
				convertedJsonContent
						.set("streaming", (jsonContent.get("pl1$streaming") != null && !jsonContent
								.get("pl1$streaming").isNull()) ? jsonContent.get("pl1$streaming").asBoolean() : true);
			}

			convertedJsonContent
					.set(WynkUtil.WYNKSTUDIO_JSON_FIELD_IMDBRATING, (jsonContent.get("pl1$imdbRating") != null && !jsonContent.get("pl1$imdbRating")
							.isNull()) ? jsonContent.get("pl1$imdbRating").asString() : "N/A");
			jsonContent.remove("pl1$imdbRating");
			// convertedJsonContent.set(imdbRating, "");
			convertedJsonContent = createSingtelPack(jsonContent, convertedJsonContent);

			if (jsonContent.get("pl2$trailerUrl") != null && !jsonContent.get("pl2$trailerUrl").isNull()) {
				trailerUrlJsonArray = (JsonArray) jsonContent.get("pl2$trailerUrl");
				if (trailerUrlJsonArray.size() > 0) {
					convertedJsonContent.add("trailerUrl", trailerUrlJsonArray.get(0));
				}
			} else {
				convertedJsonContent.add("trailerUrl", "");
			}

			if (convertedJsonContent.get("id").asString().contains("SONY")
					&& (convertedJsonContent.get("media") != null && !convertedJsonContent.get("media").isNull())) {
				JsonArray mediaArray = convertedJsonContent.get("media").asArray();
				if(mediaArray.size() > 0){
					JsonObject mediaObject = mediaArray.get(0).asObject();
					String asyncUrl = mediaObject.get("url").asString() + "/12345abcde/";
					convertedJsonContent.add("asyncUrl", asyncUrl);
				}
			}

			tagsJsonArray = (jsonContent.get("tags") != null && !jsonContent.get("tags").isNull()) ? jsonContent.get(
					"tags").asArray() : null;

			JsonArray categoryJsonArray = null;
			if (tagsJsonArray != null && tagsJsonArray.size() > 0) {
				for (int i = 0; i < tagsJsonArray.size(); i++) {
					tagsJsonObject = (JsonObject) tagsJsonArray.get(i);
					if (tagsJsonObject.get("scheme").asString().equals("Provider")) {
						contentProvidersJsonArray = createContentProviderDetails(tagsJsonObject, jsonContent,
								convertedJsonContent);
						cpTagsJsonObject = tagsJsonObject;
						provider = tagsJsonObject.get("title").asString();
					} else if (tagsJsonObject.get("scheme").asString().equalsIgnoreCase("Category")
							|| tagsJsonObject.get("scheme").asString().equalsIgnoreCase("genre")) {
						if (categoryJsonArray == null && tagsJsonObject.get("title") != null
								&& !tagsJsonObject.get("title").isNull()) {
							categoryJsonArray = new JsonArray();
							categoryJsonArray.add(appendTagToContentProviderCategories(tagsJsonObject, jsonContent,
									convertedJsonContent));
						} else {
							if (tagsJsonObject.get("title") != null && !tagsJsonObject.get("title").isNull()) {
								categoryJsonArray.add(appendTagToContentProviderCategories(tagsJsonObject, jsonContent,
										convertedJsonContent));
							}
						}
					}
				}

				convertedJsonContent.set("contentProviders",
						contentProvidersJsonArray != null ? contentProvidersJsonArray : new JsonArray());
				convertedJsonContent.set(
						"categories",
						categoryJsonArray != null ? categoryJsonArray : convertedJsonContent.get("categories") != null
								&& !convertedJsonContent.get("categories").isNull() ? convertedJsonContent
								.get("categories") : new JsonArray());

				JsonObject pricingObject = JsonObject.readFrom(AppgridHelper.appGridMetadata.get("cp_pricing_formats")
						.asString());

				if (cpTagsJsonObject != null
						&& (cpTagsJsonObject.get("title") != null && !cpTagsJsonObject.get("title").isNull())) {
					if (cpTagsJsonObject.get("title").asString().toUpperCase().equals("EROSNOW")) {
						if ((jsonContent.get("pl1$isFree") != null && !jsonContent.get("pl1$isFree").isNull())
								&& jsonContent.get("pl1$isFree").asBoolean()) {
							convertedJsonContent.set("pricingType",
									pricingObject.get("EROSNOW_FREE").asObject().get("pricingType").asString());
							convertedJsonContent.set("pricing",
									pricingObject.get("EROSNOW_FREE").asObject().get("pricing").asObject());
						} else {
							convertedJsonContent.set("pricingType",
									pricingObject.get("EROSNOW").asObject().get("pricingType").asString());
							convertedJsonContent.set("pricing", pricingObject.get("EROSNOW").asObject().get("pricing")
									.asObject());
						}
					} else if (cpTagsJsonObject.get("title").asString().toUpperCase().equals("SINGTEL")) {
						convertedJsonContent.set("download", (jsonContent.get("pl1$download") != null && !jsonContent
								.get("pl1$download").isNull()) ? jsonContent.get("pl1$download").asBoolean() : true);
						convertedJsonContent.set("download", false);
						if (convertedJsonContent.get("download").asBoolean()) {
							convertedJsonContent.set("pricingType",
									pricingObject.get("SINGTEL").asObject().get("pricingType").asString());
							convertedJsonContent.set("pricing", pricingObject.get("SINGTEL").asObject().get("pricing")
									.asObject());
						} else {
							convertedJsonContent.set("pricingType", pricingObject.get("SINGTEL_NO_DOWNLOAD").asObject()
									.get("pricingType").asString());
							convertedJsonContent.set("pricing", pricingObject.get("SINGTEL_NO_DOWNLOAD").asObject()
									.get("pricing").asObject());
						}

					} else if (cpTagsJsonObject.get("title").asString().toUpperCase().equals("SONYLIV")) {
						if ((jsonContent.get("pl1$isFree") != null && !jsonContent.get("pl1$isFree").isNull())
								&& jsonContent.get("pl1$isFree").asBoolean()) {
							convertedJsonContent.set("pricingType",
									pricingObject.get("SONYLIV_FREE").asObject().get("pricingType").asString());
							convertedJsonContent.set("pricing",
									pricingObject.get("SONYLIV_FREE").asObject().get("pricing").asObject());
						} else {
							convertedJsonContent.set("pricingType",
									pricingObject.get("SONYLIV").asObject().get("pricingType").asString());
							convertedJsonContent.set("pricing", pricingObject.get("SONYLIV").asObject().get("pricing")
									.asObject());
						}
//						convertedJsonContent.set("pricingType",
//								pricingObject.get("SONYLIV").asObject().get("pricingType").asString());
//						String pricing = "";
//						if ((convertedJsonContent.get("programType") != null && !convertedJsonContent
//								.get("programType").isNull())
//								&& convertedJsonContent.get("programType").asString().toUpperCase().equals("EPISODE")) {
//							pricing = pricingObject.get("SONYLIV_EPISODE").asObject().get("pricing").asObject()
//									.toString();
//						} else {
//							pricing = pricingObject.get("SONYLIV").asObject().get("pricing").asObject().toString();
	//					}
//						convertedJsonContent.set("pricing", JsonObject.readFrom(pricing));
					} else if (cpTagsJsonObject.get("title").asString().toUpperCase().equals("DAILYMOTION")) {
						convertedJsonContent.set("pricingType",
								pricingObject.get("DAILYMOTION").asObject().get("pricingType").asString());
						convertedJsonContent.set("pricing", pricingObject.get("DAILYMOTION").asObject().get("pricing")
								.asObject());
					} else {
						convertedJsonContent.set("pricingType",
								pricingObject.get("DEFAULT").asObject().get("pricingType").asString());
						convertedJsonContent.set("pricing", pricingObject.get("DEFAULT").asObject().get("pricing")
								.asObject());
					}
				}
			}
			if(movieJsonObject.get("title").asString().toLowerCase().contains("banner") && (convertedJsonContent.get("pl1$isPromo") != null && !convertedJsonContent.get("pl1$isPromo").isNull()) && convertedJsonContent.get("pl1$isPromo").isBoolean()){
			    convertedJsonContent.set("isPromo",convertedJsonContent.get("pl1$isPromo") );
			    convertedJsonContent.remove("pl1$isPromo");
			    }else{
			     if(convertedJsonContent.get("pl1$isPromo") != null && !convertedJsonContent.get("pl1$isPromo").isNull() && convertedJsonContent.get("pl1$isPromo").isBoolean()){
			      convertedJsonContent = null; 
			      entries.remove(j);
			    }
			     
			    }
			   if(convertedJsonContent != null){
			    entries.set(j, convertedJsonContent);
			   }
			convertedJsonContent = convertedJsonContent != null ? removeUnwantedFieldFromConvertedJson(convertedJsonContent) : null;
			if(convertedJsonContent != null && movieJsonObject.get("title").asString().toLowerCase().contains("banner") && (convertedJsonContent.get("pl1$isPromo") != null && !convertedJsonContent.get("pl1$isPromo").isNull()) && convertedJsonContent.get("pl1$isPromo").isBoolean()){
				convertedJsonContent.set("isPromo",convertedJsonContent.get("pl1$isPromo") );
				convertedJsonContent.remove("pl1$isPromo");
				}else{
					if(convertedJsonContent != null && convertedJsonContent.get("pl1$isPromo") != null && !convertedJsonContent.get("pl1$isPromo").isNull() && convertedJsonContent.get("pl1$isPromo").isBoolean()){
						convertedJsonContent = null;	
						entries.remove(j);
				}
					
				}
			if(convertedJsonContent != null){
				entries.set(j, convertedJsonContent);
			}
			if(cpSorting != null && !cpSorting.isEmpty()){
				if(provider != null && provider.toUpperCase().equals("EROSNOW")){
					erosNowJsonArray.add(convertedJsonContent); 
				}else if(provider != null && provider.toUpperCase().equals("SINGTEL")){
					singtelJsonArray.add(convertedJsonContent); 
				}else if(provider != null && provider.toUpperCase().equals("SONYLIV")){
					sonyLivJsonArray.add(convertedJsonContent); 
				}else if(provider != null && provider.toUpperCase().equals("DAILYMOTION")){
					dailymotionJsonArray.add(convertedJsonContent); 
				}else if(provider != null && provider.toUpperCase().equals("YOUTUBE")){
					youtubeJsonArray.add(convertedJsonContent); 
				}
			}
			jsonContent = null;
		}
		if(cpSorting != null && !cpSorting.isEmpty()){
			String entrieArry = arrangeContentByCpWiseCountDecendingOrdering(erosNowJsonArray, singtelJsonArray, sonyLivJsonArray, dailymotionJsonArray, youtubeJsonArray).toJSONString();
			entries = JsonArray.readFrom(entrieArry);
		}
		movieJsonObject.set(WynkUtil.WYNKSTUDIO_JSON_FIELD_ENTRIES, entries);
		// convertedJsonObject.set(entry, entries);
		// return convertedJsonObject.toString();
		movieJsonObject = updateMainJsonContent(movieJsonObject);
		return movieJsonObject.toString();
	}
	
	 @SuppressWarnings("unchecked")
	private static JSONArray arrangeContentByCpWiseCountDecendingOrdering(JSONArray erosNowJsonArray,
			 JSONArray singtelJsonArray, JSONArray sonyLivJsonArray, JSONArray dailymotionJsonArray,
			 JSONArray youtubeJsonArray) {
		 JSONArray entries = new JSONArray();
		   HashMap<String, Integer> cpListingCount = new HashMap<String, Integer>();
		   cpListingCount.put("erosnow", erosNowJsonArray.size());
		   cpListingCount.put("singtel", singtelJsonArray.size());
		   cpListingCount.put("sonyliv", sonyLivJsonArray.size());
		   cpListingCount.put("dailymotion", dailymotionJsonArray.size());
		   cpListingCount.put("youtube", youtubeJsonArray.size());
	        Set<Entry<String, Integer>> set = cpListingCount.entrySet();
	        List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(
	                set);
	        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
	            public int compare(Map.Entry<String, Integer> o1,
	                    Map.Entry<String, Integer> o2) {
	                return o2.getValue().compareTo(o1.getValue());
	            }
	        });

	        for (Entry<String, Integer> entry : list) {
	        	if(entry.getKey().equalsIgnoreCase("erosnow")){
	        		entries.addAll(erosNowJsonArray);
	        	}else if(entry.getKey().equalsIgnoreCase("singtel")){
	        		entries.addAll(singtelJsonArray);
	        	}else if(entry.getKey().equalsIgnoreCase("sonyliv")){
	        		entries.addAll(sonyLivJsonArray);
	        	}else if(entry.getKey().equalsIgnoreCase("dailymotion")){
	        		entries.addAll(dailymotionJsonArray);
	        	}else if(entry.getKey().equalsIgnoreCase("youtube")){
	        		entries.addAll(youtubeJsonArray);
	        	}

	        }
		return entries;
	}


	private static JsonObject createSingtelPack(JsonObject jsonContent, JsonObject convertedJsonContent) {
		if ((jsonContent.get("pl2$pack") != null && !jsonContent.get("pl2$pack").isNull())
				|| (jsonContent.get("pl1$pack") != null && !jsonContent.get("pl1$pack").isNull())) {
			String pack = "";
			if (jsonContent.get("pl2$pack") != null && !jsonContent.get("pl2$pack").isNull()
					&& jsonContent.get("pl2$pack").isArray()) {
				pack = jsonContent.get("pl2$pack").asArray().get(0).asString();
			} else {
				pack = jsonContent.get("pl1$pack").asString();
			}
			String[] packs = pack.split("~");
			JsonArray packArray = new JsonArray();
			for (int i = 0; i < packs.length; i++) {
				if (packs[i].toLowerCase().contains("lite")) {
					packArray.add("lite");
				}
			}
			// String[] fromDates =
			// jsonContent.get("pl1$fromDate").asString().split("~");
			// String[] toDates =
			// jsonContent.get("pl1$toDate").asString().split("~");
			convertedJsonContent.add("pack", packArray);
		}
		return convertedJsonContent;
	}

	private static JsonObject updateMainJsonContent(JsonObject movieJsonObject) {
		movieJsonObject = movieJsonObject.get("totalResults") != null && !movieJsonObject.get("totalResults").isNull() ? movieJsonObject
				.set("totalCount", movieJsonObject.get("totalResults")) : movieJsonObject;
		movieJsonObject = movieJsonObject.get("itemsPerPage") != null && !movieJsonObject.get("itemsPerPage").isNull() ? movieJsonObject
				.set("pageSize", movieJsonObject.get("itemsPerPage")) : movieJsonObject;
		movieJsonObject.remove("$xmlns");
		movieJsonObject.remove("totalResults");
		movieJsonObject.remove("itemsPerPage");
		movieJsonObject.remove("startIndex");
		movieJsonObject.remove("entryCount");
		movieJsonObject.remove("title");
		movieJsonObject.remove("cp_sorting");
		return movieJsonObject;
	}

	private static JsonArray createContentProviderDetails(JsonObject tagsJsonObject, JsonObject jsonContent,
			JsonObject convertedJsonContent) {
		JsonArray contentProvidersJsonArray;
		String contentProvider;
		JsonObject contentProviderJsonObject = new JsonObject();
		contentProvider = tagsJsonObject.get("title").asString().toUpperCase();
		contentProviderJsonObject.add("cpId", contentProvider);
		contentProviderJsonObject.add("name", contentProvider);
		contentProviderJsonObject.add("assetId", convertedJsonContent.get("id"));
		contentProvidersJsonArray = new JsonArray();
		contentProvidersJsonArray.add(contentProviderJsonObject);
		return contentProvidersJsonArray;
	}

	private static JsonObject appendTagToContentProviderCategories(JsonObject tagsJsonObject, JsonObject jsonContent,
			JsonObject convertedJsonContent) {
		JsonArray contentProvidersJsonArray;
		String contentProvider;
		JsonObject contentProviderJsonObject = new JsonObject();
		contentProvider = tagsJsonObject.get("title").asString();
		contentProviderJsonObject.add("id", contentProvider);
		contentProviderJsonObject.add("title", contentProvider);
		contentProvidersJsonArray = new JsonArray();
		contentProvidersJsonArray.add(contentProviderJsonObject);
		return contentProviderJsonObject;
	}

	public static JsonObject appendMediaThumbnailImageUrlToImages(JsonObject actualJsonObject, JsonObject convertedJson) {
		JsonObject imagesJsonObject = new JsonObject();
		if (actualJsonObject.get("media").asArray().size() > 0) {
			JsonArray thumbnailJsonArray = actualJsonObject.get("media").asArray().get(0).asObject().get("thumbnails")
					.asArray();
			JsonObject imageJsonObject = null;
			JsonObject jsonObject;
			for (int i = 0; i < thumbnailJsonArray.size(); i++) {
				jsonObject = thumbnailJsonArray.get(i).asObject();
				if (jsonObject.get("bitrate").toString().equals("1")) {
					imageJsonObject = new JsonObject();
					imageJsonObject.add("url", jsonObject.get("streamingUrl"));
					imagesJsonObject.add("landscape", imageJsonObject);
				} else if (jsonObject.get("bitrate").toString().equals("2")) {
					imageJsonObject = new JsonObject();
					imageJsonObject.add("url", jsonObject.get("streamingUrl"));
					imagesJsonObject.add("portrait", imageJsonObject);
				}
			}
		}
		convertedJson.set("images", imagesJsonObject);
		return convertedJson;
	}

	private static JsonObject appendThumbnailImageUrlToImages(JsonObject convertedJson) {
		JsonObject thumbnailJsonObject = convertedJson.get("thumbnails").asObject();
		JsonObject imagesJsonObject = new JsonObject();
		if (thumbnailJsonObject != null && !thumbnailJsonObject.isNull() && thumbnailJsonObject.get("poster") != null
				&& !thumbnailJsonObject.get("poster").isNull()) {
			thumbnailJsonObject = thumbnailJsonObject.get("poster").asObject();
			JsonObject portraitJsonObject = new JsonObject();
			portraitJsonObject.set("url", thumbnailJsonObject.get("url"));
			portraitJsonObject.set("width", thumbnailJsonObject.get("width"));
			portraitJsonObject.set("height", thumbnailJsonObject.get("height"));
			imagesJsonObject.set("portrait", portraitJsonObject);
			convertedJson.set("images", imagesJsonObject);
		}
		return convertedJson;
	}

	private static JsonObject convertMpxJsonToSpecFormat(JsonObject jsonObject) {
		jsonObject.set("mpxid", jsonObject.get("id"));
		jsonObject.set("id", jsonObject.get("guid"));
		jsonObject.set("guid", jsonObject.get("mpxid"));
		jsonObject.remove("mpxid");
		// jsonObject.set("releaseYear", 0);
		// jsonObject.set("runtime", jsonObject.get("runtime") != null &&
		// !jsonObject.get("runtime").isNull() &&
		// !jsonObject.get("runtime").asString().isEmpty() ?
		// jsonObject.get("runtime") : 0);
		if (jsonObject.get("year") != null && !jsonObject.get("year").isNull()) {
			jsonObject.set("releaseYear", jsonObject.get("year"));
		}
		jsonObject.set("releaseDate",
				jsonObject.get("pubDate") != null && !jsonObject.get("pubDate").isNull() ? jsonObject.get("pubDate")
						.asString() : "");
		jsonObject = createClassification(jsonObject);
		jsonObject = jsonObject.get("tags") != null && !jsonObject.get("tags").isNull() ? jsonObject
				: createDefaultTags(jsonObject);
		jsonObject = createDefaultCategory(jsonObject);
		jsonObject = createSeriesDetails(jsonObject);
		return jsonObject;
	}

	private static JsonObject removeUnwantedFieldFromConvertedJson(JsonObject jsonObject) {
		jsonObject.remove("totalResults");
		jsonObject.remove("itemsPerPage");
		jsonObject.remove("pubDate");
		jsonObject.remove("rating");
		jsonObject.remove("availableTvSeasonIds");
		jsonObject.remove("year");
		jsonObject.remove("thumbnails");
		jsonObject.remove("updated");
		jsonObject.remove("shortDescription");
		jsonObject.remove("seriesEpisodeNumber");
		jsonObject.remove("pl1$assetId");
		jsonObject.remove("pl1$imdbRating");
		jsonObject.remove("pl1$isFree");
		jsonObject.remove("pl2$trailerUrl");
		jsonObject.remove("pl1$download");
		jsonObject.remove("pl1$streaming");
		jsonObject.remove("pl1$expiryTsMs");
		jsonObject.remove("pl1$pack");
		jsonObject.remove("pl2$pack");
		jsonObject.remove("pl1$toDate");
		jsonObject.remove("pl1$fromDate");

		return jsonObject;

	}

	private static JsonObject createClassification(JsonObject jsonObject) {
		JsonArray classificationsJsonArray = new JsonArray();
		JsonObject classificationJsonObject = new JsonObject();
		jsonObject = jsonObject.get("rating") != null && !jsonObject.get("rating").isNull() ? classificationJsonObject
				.set("rating", jsonObject.get("rating").asString()) : jsonObject;
		classificationJsonObject.set("scheme", "");
		classificationJsonObject.set("consumerAdvice", "");
		classificationsJsonArray.add(classificationJsonObject);
		jsonObject.set("classifications", classificationsJsonArray);
		classificationsJsonArray = null;
		return jsonObject;
	}

	private static JsonObject createDefaultTags(JsonObject jsonObject) {
		JsonArray tagsJsonArray = new JsonArray();
		JsonObject tagJsonObject = new JsonObject();
		tagJsonObject.set("url", "");
		tagJsonObject.set("scheme", "genre");
		tagsJsonArray.add(tagJsonObject);
		jsonObject.set("tags", tagsJsonArray);
		tagsJsonArray = null;
		return jsonObject;
	}

	private static JsonObject createDefaultCategory(JsonObject jsonObject) {
		JsonArray categoriesJsonArray = new JsonArray();
		JsonObject categoryObject = new JsonObject();
		categoryObject.set("id", "featured");
		categoryObject.set("title", "featured");
		categoryObject.set("description", "");
		categoriesJsonArray.add(categoryObject);
		jsonObject.set("categories", categoriesJsonArray);
		categoriesJsonArray = null;
		return jsonObject;
	}

	private static JsonObject createSeriesDetails(JsonObject jsonObject) {
		jsonObject = jsonObject.get("seriesId") != null && !jsonObject.get("seriesId").isNull() ? jsonObject
				: jsonObject.remove("seriesId");
		jsonObject = jsonObject.get("availableTvSeasonIds") != null && !jsonObject.get("availableTvSeasonIds").isNull() ? jsonObject
				.set("seasons", jsonObject.get("availableTvSeasonIds")) : jsonObject.remove("availableTvSeasonIds");
		jsonObject = jsonObject.get("tvSeasonNumber") != null && !jsonObject.get("tvSeasonNumber").isNull() ? jsonObject
				: jsonObject.remove("tvSeasonNumber");
		jsonObject = jsonObject.get("tvSeasonId") != null && !jsonObject.get("tvSeasonId").isNull() ? jsonObject
				: jsonObject.remove("tvSeasonId");
		jsonObject = jsonObject.get("tvSeasonEpisodeNumber") != null
				&& !jsonObject.get("tvSeasonEpisodeNumber").isNull() ? jsonObject : jsonObject
				.remove("tvSeasonEpisodeNumber");
		if (jsonObject.get("seriesTvSeasons") != null && !jsonObject.get("seriesTvSeasons").isNull()) {
			JsonArray seriesTvSeasonsArray = (JsonArray) (jsonObject.get("seriesTvSeasons").asArray().size() > 0
					&& jsonObject.get("seriesTvSeasons").asArray().size() == 1
							? jsonObject.get("seriesTvSeasons").asArray() : JsonArray.readFrom(jsonSortByValue(jsonObject.get("id").asString().contains("SONY") ? "startYear" : "tvSeasonNumber", jsonObject.get("id").asString().contains("SONY") ? 1 : 0, jsonObject.get("seriesTvSeasons").asArray())));
			int index = 0;
			String seasonId = null;
			for (int i = 0; i < seriesTvSeasonsArray.size(); i++) {
				if (seriesTvSeasonsArray.get(i) != null && !seriesTvSeasonsArray.get(i).isNull()) {
					index = seriesTvSeasonsArray.get(i).asObject().get("id").asString().lastIndexOf("/");
					seasonId = seriesTvSeasonsArray.get(i).asObject().get("id").asString().substring(index + 1);
					seriesTvSeasonsArray.get(i).asObject().set("id", seasonId);
					seriesTvSeasonsArray.get(i).asObject().remove("guid");
					seriesTvSeasonsArray.get(i).asObject().remove("startYear");
					seriesTvSeasonsArray.get(i).asObject().remove("tvSeasonNumber");
				}
			}
			jsonObject.set("seriesTvSeasons", seriesTvSeasonsArray);
		} else {
			jsonObject.set("seriesTvSeasons", new JsonArray());
		}

		return jsonObject;
	}

	private static String jsonSortByValue(final String sortKey, final int sortOrder, JsonArray inputJsonArray) {
		List<JsonObject> jsonValues = new ArrayList<JsonObject>();
		try {
			for (int i = 0; i < inputJsonArray.size(); i++) {
				jsonValues.add(inputJsonArray.get(i).asObject());
			}
			Collections.sort(jsonValues, new Comparator<JsonObject>() {
				public int compare(JsonObject a, JsonObject b) {
					Integer valA = 0;
					Integer valB = 0;

					valA = a.get(sortKey).asInt();
					valB = b.get(sortKey).asInt();

					return (sortOrder == 1 ? -valA.compareTo(valB) : valA.compareTo(valB));
					// if you want to change the sort order, simply use the
					// following:
					// return -valA.compareTo(valB); desc
					// return valA.compareTo(valB); asc
				}
			});
		} catch (Exception e) {
		}
		return jsonValues.toString();
	}
 
}
