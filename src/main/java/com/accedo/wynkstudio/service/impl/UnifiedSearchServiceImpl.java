package com.accedo.wynkstudio.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.accedo.wynkstudio.common.CPConstants;
import com.accedo.wynkstudio.exception.BusinessApplicationException;
import com.accedo.wynkstudio.helper.AppgridHelper;
import com.accedo.wynkstudio.helper.ContentProviderHelper;
import com.accedo.wynkstudio.service.UnifiedSearchService;
import com.accedo.wynkstudio.util.ContentProviderUtil;
import com.accedo.wynkstudio.util.JsonTransformation;
import com.accedo.wynkstudio.util.Util;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

@Service
public class UnifiedSearchServiceImpl implements UnifiedSearchService {

	@Autowired
	private MessageSource messageSource;

	private HttpHeaders headers;
	private JsonObject mpxFeedJson;
	final Logger log = LoggerFactory.getLogger(this.getClass());

	@PostConstruct
	public void init() {
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public String getSearchList(final String searchKey, String cpIds, String languages, String genres,
			String subscription) {
		String responseResult = "";
		String empty = "{ entries :[] }";
		mpxFeedJson = AppgridHelper.mpxFeedData;
		String creditsByProgramIdsUrl = mpxFeedJson.get("creditsbyprogramid").asString();
		final ExecutorService es = Executors.newFixedThreadPool(4);
		try {
			final String languageCode = Util.getLanguageFromSearchKey(searchKey);
			final String providerIds = getAllowedCPIdsWithSubscription(cpIds, subscription.toUpperCase());
			final String language = languageCode != null ? languageCode : languages.replace(",", "|");
			final String genre = genres.replace(",", "|");
			final String[] searchTypes = AppgridHelper.appGridMetadata.get("unifiedsearch_by_type_list").asString()
					.split("~");
			final String range = AppgridHelper.appGridMetadata.get("unified_serach_range").asString();
			List<Future<String>> futures = new ArrayList<Future<String>>();
			final int typesLength = searchTypes.length+1;
			for (int i = -1; i < typesLength; i++) {
				final int j = i;
				futures.add(es.submit(new Callable() {
					public Object call() throws Exception {
						if(languageCode != null){
						return searchTypes.length == j ? getUnifiedSearchList(searchKey, j, providerIds, "", genre,
							"", range, null, es) : getUnifiedSearchList(searchKey, j, providerIds, language, genre,
								j != -1 ? searchTypes[j] : "", range, languageCode, es);
						}else{
							return searchTypes.length == j ? null : getUnifiedSearchList(searchKey, j, providerIds, language, genre,
									j != -1 ? searchTypes[j] : "", range, languageCode, es);
						}
					}
				}));
			}

			JSONObject responseJsonObject = null;
			JSONArray jsonArray = null;
			JSONArray nextJsonArray = null;
			JSONArray exactMatchingEntries = null;
			List<String> list = null;
			for (Future future : futures) {
				Future<String> futureResponse = (Future<String>) future.get();
				if (futureResponse != null && futureResponse.get() != null && !futureResponse.get().isEmpty()) {
					JSONParser jsonParser = new JSONParser();
					if (responseJsonObject != null) {
						JSONObject nextJsonObject = (JSONObject) jsonParser.parse(futureResponse.get());
						nextJsonArray = (JSONArray) nextJsonObject.get("entries");
						if (nextJsonArray != null && nextJsonArray.size() > 0) {
							list = getExactMatchingEntries(nextJsonArray, searchKey, list.get(2));
							if (Integer.parseInt(list.get(0)) != 0) {
								exactMatchingEntries = exactMatchingEntries != null ? exactMatchingEntries : new JSONArray();
								exactMatchingEntries.addAll((JSONArray) jsonParser.parse(list.get(1)));
							} else {
								jsonArray = jsonArray != null ? jsonArray : new JSONArray();
								jsonArray.addAll((JSONArray) jsonParser.parse(list.get(1)));
							}
						}
					} else {
						responseJsonObject = (JSONObject) jsonParser.parse(futureResponse.get());
						nextJsonArray = (JSONArray) responseJsonObject.get("entries");
						list = getExactMatchingEntries(nextJsonArray, searchKey, "");
						if (Integer.parseInt(list.get(0)) != 0) {
							exactMatchingEntries = (JSONArray) jsonParser.parse(list.get(1));
						} else {
							jsonArray = (JSONArray) jsonParser.parse(list.get(1));
						}
					}
				}
			}
			
			es.shutdown(); // Terminate Thread
			if (exactMatchingEntries != null && exactMatchingEntries.size() > 0) {
				if(languageCode != null){
					exactMatchingEntries.addAll(jsonArray);
				}
				responseJsonObject.put("entries", exactMatchingEntries  );
				responseJsonObject.put("exactMatch", true);
			} else {
				responseJsonObject.put("entries", jsonArray);
				responseJsonObject.put("exactMatch", false);
			}
			responseResult = responseJsonObject.toJSONString();
			responseResult = ContentProviderUtil.getProgramWithAvailableCredits(responseResult, creditsByProgramIdsUrl,
					headers);
			responseResult = ContentProviderHelper.createJsonResponse(responseResult);
		} catch (Exception e) {
			log.error("Search Error Log, line 130: ", e);
			responseResult = empty;
			es.shutdown();
//			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}finally {
			es.shutdown();
		}

	return responseResult;
	}

	@Async
	private Future<String> getUnifiedSearchList(String searchKey, int j, String providerIds, String languages,
			String genres, String searchType, String range, String languageCode, ExecutorService es) {
		String responseResult = "";
		mpxFeedJson = AppgridHelper.mpxFeedData;
		String url = "";
		if (j == -1) {
			return getPeopleDetailsBySearch(searchKey);
		}
		if (searchKey.contains(" ")) {
			searchKey = languageCode != null ? "" : searchKey; 
			url = mpxFeedJson.get("searchbymultiplekey").asString();
			url = url.replace("{2}", providerIds + ",").replace("{0}", searchKey).replace("{1}", searchKey)
					.replace("{3}", range).replace("{4}", searchType).replace("{5}", languages).replace("{6}", genres);
			url = languageCode != null ? url + "&sort=year|desc" : url;
		} else {
			searchKey = languageCode != null ? "" : searchKey; 
			url = mpxFeedJson.get("searchbysinglekey").asString();
			url = url.replace("{2}", providerIds + ",").replace("{0}", searchKey).replace("{1}", searchKey)
					.replace("{3}", range).replace("{4}", searchType).replace("{5}", languages).replace("{6}", genres);
			url = languageCode != null ? url + "&sort=year|desc" : url;
		}

		try {

			responseResult = Util.executeApiGetCall(url);
		} catch (HttpClientErrorException e) {
			log.error("Search Impl- Http Exception- Line 166: ", e);
			es.shutdown();
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		}

		return new AsyncResult<String>(responseResult);
	}

	public String getSearchListByProgramType(String searchKey, String programType, String cpIds, String languages,
			String genres, String totalPageSize, String pageSize, String pageNumber, String subscription) {
		String responseResult = "";
		String empty = "{ entries :[] }";
		String url = "";
		String range = "";
		mpxFeedJson = AppgridHelper.mpxFeedData;
		String creditsByProgramIdsUrl = mpxFeedJson.get("creditsbyprogramid").asString();
		try {

			final String providerIds = getAllowedCPIdsWithSubscription(cpIds, subscription.toUpperCase());
			responseResult = ContentProviderUtil.createPagination(totalPageSize, pageSize, pageNumber, messageSource
					.getMessage(CPConstants.WYNKSTUDIO_MPX_PAGINATION_DEFAULT_START, null, "", Locale.ENGLISH),
					messageSource.getMessage(CPConstants.WYNKSTUDIO_MPX_PAGINATION_DEFAULT_END, null, "",
							Locale.ENGLISH));
			String[] ranges = responseResult != null ? !responseResult.isEmpty() ? responseResult.trim().split("_")
					: null : null;
			if (ranges != null && ranges.length > 1) {
				range = ranges[0] + "-" + ranges[1];
			}
			else
			{
				range = "1-10";
			}

			programType = programType.equalsIgnoreCase(messageSource.getMessage(
					CPConstants.WYNKSTUDIO_PROGRAMTYPE_VIDEO, null, "", Locale.ENGLISH)) ? messageSource.getMessage(
					CPConstants.WYNKSTUDIO_PROGRAMTYPE_OTHER, null, "", Locale.ENGLISH) : programType;
			if (programType.equalsIgnoreCase(messageSource.getMessage(CPConstants.WYNKSTUDIO_PROGRAMTYPE_PEOPLE, null,
					"", Locale.ENGLISH))) {
				url = mpxFeedJson.get("peoplesearch").asString();
				url = url.replace("{0}", searchKey);
				url = url + "&range=" + range;
				url = url + "&byCredits=byCreditType=actor|director";
				responseResult = Util.executeApiGetCall(url);
			} else {
				final String languageCode = Util.getLanguageFromSearchKey(searchKey);
				if (languageCode != null) {
					responseResult = serachByLanguage(searchKey, programType, languages, genres, range, languageCode, providerIds,Integer.parseInt(pageNumber));
				} else {
					if (searchKey.contains(" ")) {
						url = mpxFeedJson.get("searchbymultiplekey").asString();
						url = url.replace("{2}", providerIds + ",").replace("{0}", searchKey).replace("{1}", searchKey)
								.replace("{3}", range).replace("{4}", programType).replace("{5}", languages)
								.replace("{6}", genres);
					} else {
						url = mpxFeedJson.get("searchbysinglekey").asString();
						url = url.replace("{2}", providerIds + ",").replace("{0}", searchKey).replace("{1}", searchKey)
								.replace("{3}", range).replace("{4}", programType).replace("{5}", languages)
								.replace("{6}", genres);
					}
					responseResult = Util.executeApiGetCall(url);
				}
			}
			if (responseResult != null) {
				JsonObject responseJsonObject = null;
				responseJsonObject = JsonObject.readFrom(responseResult);
				if (responseJsonObject
						.get(messageSource.getMessage(CPConstants.WYNKSTUDIO_JSON_FIELD_ENTRIES, null, "",
								Locale.ENGLISH)) != null
						&& !responseJsonObject.get(messageSource.getMessage(CPConstants.WYNKSTUDIO_JSON_FIELD_ENTRIES,
								null, "", Locale.ENGLISH)).isNull()) {
					responseResult = responseJsonObject.toString();
					responseResult = ContentProviderUtil.getProgramWithAvailableCredits(responseResult,
							creditsByProgramIdsUrl, headers);
					responseResult = ContentProviderHelper.createJsonResponse(responseResult);
				}
			}
		} catch (Exception e) {
			log.error("Search Impl- Line 226- Error Log: ", e);
			responseResult = empty;
		//	throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return responseResult;
	}

	public String getPeopleShows(String searchKey, String providerIds) {
		String responseResult = "";
		String id = "";
		providerIds = providerIds.replace(",", "|");
		String peopleJson = getPeopleDetails(searchKey, false);
		JSONParser searchParser = new JSONParser();
		try {
			JSONObject convertedJsonObject = (JSONObject) searchParser.parse(peopleJson);
			JSONArray creditsJsonArray = (JSONArray) convertedJsonObject.get("entries");
			JSONObject entries = (JSONObject) creditsJsonArray.get(0);
			id = (String) entries.get("id");
		} catch (ParseException e1) {
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e1, e1.getMessage());
		}
		String url = "";
		mpxFeedJson = AppgridHelper.mpxFeedData;
		url = mpxFeedJson.get("searchpeoplerelated").asString();
		url = url.replace("{1}", providerIds).replace("{0}", id);
		try {
			responseResult = Util.executeApiGetCall(url);
			if (responseResult != null && !responseResult.isEmpty()) {
				responseResult = ContentProviderHelper.createJsonResponse(responseResult);
			}
		} catch (HttpClientErrorException e) {
			log.error("Search Impl- Http Exception- Line 277: ", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (Exception e) {
			log.error("Search Impl- Exception- Line 286: ", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return responseResult;
	}

	public String getPeople(String searchKey) {
		return getPeopleDetails(searchKey, false);
	}

	private String getPeopleDetails(String searchKey, Boolean exact) {
		String responseResult = "";
		mpxFeedJson = AppgridHelper.mpxFeedData;
		String url = "";
		if (exact) {
			url = mpxFeedJson.get("peoplesearch_exact").asString();
		} else {
			url = mpxFeedJson.get("peoplesearch").asString();
		}
		url = url.replace("{0}", searchKey);
		try {
			responseResult = Util.executeApiGetCall(url);
			if (responseResult != null && !responseResult.isEmpty()) {
				responseResult = JsonTransformation.transformJson(responseResult, messageSource.getMessage(
						CPConstants.WYNKSTUDIO_EROSENOW_SPEC_PATH_PEOPLE, null, "", Locale.ENGLISH));
			}
		} catch (HttpClientErrorException e) {
			log.error("Search Impl- Http Exception- Line 318: ", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		}
		return responseResult;
	}

	@Async
	private Future<String> getPeopleDetailsBySearch(String searchKey) {
		String responseResult = "";
		mpxFeedJson = AppgridHelper.mpxFeedData;
		String url = "";
		final String range = AppgridHelper.appGridMetadata.get("unified_serach_range").asString();
		url = mpxFeedJson.get("peoplesearch").asString();
		url = url.replace("{0}", searchKey);
		url = url + "&range=" + range;
		url = url + "&byCredits=byCreditType=actor|director";
		try {
			responseResult = Util.executeApiGetCall(url);
		} catch (HttpClientErrorException e) {
			log.error("Search Impl- Http Exception- Line 344: ", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		}
		return new AsyncResult<String>(responseResult);
	}

	@Override
	public String getGenreList() {
		return AppgridHelper.appGridMetadata.get("cp_genre_list").asString();
	}

	@Override
	public String getLanguages() {
		return AppgridHelper.appGridMetadata.get("cp_language_list").asString();
	}

	@Override
	public String getCps() {
		return AppgridHelper.appGridMetadata.get("cp_list").asString();
	}

	private String getAllowedCPIdsWithSubscription(String cpIds, String subscription) {
		StringBuilder sb = new StringBuilder();
		if (cpIds == null || cpIds.isEmpty()) {
			sb.append(messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_SONYLIV, null, "", Locale.ENGLISH))
					.append("|")
					.append(messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_EROSNOW, null, "", Locale.ENGLISH))
					.append("|")
					.append(messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_SINGTEL, null, "", Locale.ENGLISH))
					.append("|")
					.append(messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_DAILYMOTION, null, "",
							Locale.ENGLISH))
					.append("|")
					.append(messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_YOUTUBE, null, "", Locale.ENGLISH));

			cpIds = sb.toString().toUpperCase();
		}

		if (subscription.isEmpty()) {
			cpIds = cpIds.replace(
					messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_SINGTEL, null, "", Locale.ENGLISH), "");
			cpIds = cpIds.replace(
					messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_EROSNOW, null, "", Locale.ENGLISH),
					messageSource.getMessage(CPConstants.WYNKSTUDIO_MPX_TAG_EROSNOW_FREE, null, "", Locale.ENGLISH));
			return cpIds.replace(",", "|");

		} else if (subscription.contains(messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_EROSNOW, null, "",
				Locale.ENGLISH))
				&& subscription.contains(messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_SINGTEL, null, "",
						Locale.ENGLISH))
				&& subscription.contains(messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_SONYLIV, null, "",
						Locale.ENGLISH))
				&& subscription.contains(messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_DAILYMOTION, null,
						"", Locale.ENGLISH))
				&& subscription.contains(messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_YOUTUBE, null, "",
						Locale.ENGLISH))) {

			return cpIds.replace(",", "|");

		} else if (subscription.contains(messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_EROSNOW, null, "",
				Locale.ENGLISH))
				&& subscription.contains(messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_SINGTEL, null, "",
						Locale.ENGLISH))) {
			return cpIds.replace(",", "|");

		} else if (subscription.contains(messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_EROSNOW, null, "",
				Locale.ENGLISH))) {
			cpIds = cpIds.replace(
					messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_SINGTEL, null, "", Locale.ENGLISH), "");
			return cpIds.replace(",", "|");
		} else if (subscription.contains(messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_SINGTEL, null, "",
				Locale.ENGLISH))) {
			if (cpIds.toUpperCase().contains(
					messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_EROSNOW, null, "", Locale.ENGLISH))) {

				cpIds = cpIds
						.replace(messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_EROSNOW, null, "",
								Locale.ENGLISH), messageSource.getMessage(CPConstants.WYNKSTUDIO_MPX_TAG_EROSNOW_FREE,
								null, "", Locale.ENGLISH));
				return cpIds.replace(",", "|");
			}
		}

		return cpIds.replace(",", "|");

	}

	@SuppressWarnings("unchecked")
	private List<String> getExactMatchingEntries(JSONArray jsonArray, String searchKey,String title) {
		JSONArray matchingJsonArray = new JSONArray();
		JSONArray resultJsonArray = new JSONArray();
		List<String> list = new ArrayList<String>();
		JSONObject jsonObject = null;
		StringBuilder titleBuilder = new StringBuilder();
		titleBuilder.append(title);
		for (int i = 0; i < jsonArray.size(); i++) {
			jsonObject = (JSONObject) jsonArray.get(i);
//			title = k != totalSize ? title + jsonObject.get("title").toString().toLowerCase() : title;
			if (jsonObject.get("title").toString().toLowerCase().contains(searchKey.toLowerCase()) && !titleBuilder.toString().contains(jsonObject.get("guid").toString().toLowerCase())) {
				titleBuilder.append(jsonObject.get("guid").toString().toLowerCase());
				matchingJsonArray.add(jsonObject);
			} else if (jsonObject.get("credits") != null && jsonObject.get("credits").toString().toLowerCase().contains(searchKey.toLowerCase())) {
				matchingJsonArray.add(jsonObject);
			}else{
				if(!titleBuilder.toString().contains(jsonObject.get("guid").toString().toLowerCase())){
					resultJsonArray.add(jsonObject);
					titleBuilder.append(jsonObject.get("guid").toString().toLowerCase());
				}
			}
		}
		list.add(0, String.valueOf(matchingJsonArray.size()));
		list.add(1, matchingJsonArray.size() > 0 ? matchingJsonArray.toJSONString() : resultJsonArray.toJSONString());
		list.add(2, titleBuilder.toString());
		return list;

	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String serachByLanguage(final String searchKey, String programType, String languages,
			final String genres, String range, final String languageCode, final String providerIds,final int pageNumber)
					throws InterruptedException, ExecutionException, ParseException {
		final String limit = range;
		final String programCategory = programType;
		final String language = languageCode != null ? languageCode : languages.replace(",", "|");
		final ExecutorService es = Executors.newFixedThreadPool(4);
		List<Future<String>> futures = new ArrayList<Future<String>>();
		final int loopLength = languageCode != null ? 2 : 1;
		for (int i = 0; i < loopLength; i++) {
			final int j = i;
			futures.add(es.submit(new Callable() {
				public Object call() throws Exception {
					return j == 0 ?  pageNumber == 1  ? getUnifiedSearchList(searchKey, j, providerIds, "", genres,
							programCategory, limit, null, es) : null : getUnifiedSearchList(searchKey, j, providerIds, language, genres,
									programCategory, limit, languageCode, es);
				}
			}));
		}
		
		JSONObject responseJsonObject = null;
		JSONArray jsonArray = null;
		JSONArray nextJsonArray = null;
		List<String> list = null;
		JSONParser jsonParser = new JSONParser();
		int k= 0;
		for (Future future : futures) {
			Future<String> futureResponse = (Future<String>) future.get();
			responseJsonObject = futureResponse != null ? (JSONObject) jsonParser.parse(futureResponse.get()) : null;
			if (futureResponse != null && futureResponse.get() != null && !futureResponse.get().isEmpty()) {
					JSONObject nextJsonObject = (JSONObject) jsonParser.parse(futureResponse.get());
					nextJsonArray = (JSONArray) nextJsonObject.get("entries");
					if (nextJsonArray != null && nextJsonArray.size() > 0) {
						list = getRemoveDuplecateEntries(nextJsonArray, searchKey, list != null ? list.get(1) : "", k);
						if(list.get(0) != null){
							jsonArray = jsonArray != null ? jsonArray : new JSONArray();
							jsonArray.addAll((JSONArray) jsonParser.parse(list.get(0)));
						}
					}
			}
			k++;
		}
		
		es.shutdown(); // Terminate Thread
		
		responseJsonObject.put("entries", jsonArray != null && jsonArray.size() > 0 ? jsonArray : new JsonArray());
		return responseJsonObject.toJSONString();
	}
	
	@SuppressWarnings("unchecked")
	private List<String> getRemoveDuplecateEntries(JSONArray jsonArray, String searchKey,String title, int k) {
		JSONArray resultJsonArray = new JSONArray();
		JSONArray matchingJsonArray = new JSONArray();
		List<String> list = new ArrayList<String>();
		JSONObject jsonObject = null;
		StringBuilder titleBuilder = new StringBuilder();
		titleBuilder.append(title);
		for (int i = 0; i < jsonArray.size(); i++) {
			jsonObject = (JSONObject) jsonArray.get(i);
			if (jsonObject.get("title").toString().toLowerCase().contains(searchKey.toLowerCase()) && !titleBuilder.toString().contains(jsonObject.get("guid").toString().toLowerCase())) {
				titleBuilder.append(jsonObject.get("guid").toString().toLowerCase());
				matchingJsonArray.add(jsonObject);
			}else{
			if(!titleBuilder.toString().contains(jsonObject.get("guid").toString().toLowerCase())){
				resultJsonArray.add(jsonObject);
				titleBuilder.append(jsonObject.get("guid").toString().toLowerCase());
			}
			}
		}
		list.add(0, matchingJsonArray.size() > 0 ? matchingJsonArray.toJSONString() : k == 1 ? resultJsonArray.toJSONString() : null);
		list.add(1, titleBuilder.toString());
		return list;

	}


}
