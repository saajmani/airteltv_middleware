package com.accedo.wynkstudio.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
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
import com.accedo.wynkstudio.dao.ProductDao;
import com.accedo.wynkstudio.dao.UserFilterDao;
import com.accedo.wynkstudio.entity.UserFilter;
import com.accedo.wynkstudio.entity.UserProfile;
import com.accedo.wynkstudio.exception.BusinessApplicationException;
import com.accedo.wynkstudio.helper.AppgridHelper;
import com.accedo.wynkstudio.helper.ContentProviderHelper;
import com.accedo.wynkstudio.service.DiscoverService;
import com.accedo.wynkstudio.util.ContentProviderUtil;
import com.accedo.wynkstudio.util.Util;
import com.accedo.wynkstudio.vo.ProductVO;
import com.accedo.wynkstudio.vo.UserFilterVO;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
@Service
public class DiscoverServiceImpl implements DiscoverService {

	@Autowired
	private MessageSource messageSource;
	@Autowired
	private UserFilterDao userFilterDao;
	
	@Autowired
	private ProductDao productDao;
	
	private HttpHeaders headers;
	final Logger log = LoggerFactory.getLogger(this.getClass());
	private Random random = new Random();
	@PostConstruct
	public void init() {
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	}

	@Override
	public String createFilterByUid(String uId, String userInfoJson) {
		String response = "";
		String defaultName = "";
		try {
			JsonObject userFilterJsonObject = JsonObject.readFrom(userInfoJson);
			UserFilter userFilter = createUserFilter(uId, userFilterJsonObject);
			defaultName = userFilter.getFilterType().toLowerCase().equals("movie") ? "Movies Collection " : userFilter.getFilterType().toLowerCase().equals("series") ? "TV Shows Collection " : "Shorts Collection " ;
			if (getFilterContentEmptyOrNot(userFilter.getFilter(),
					userFilter.getFilterType(),
					userFilter.getSortingKey(), "", "", "", userFilter.getLanguages(), uId) ) {
				if (uId != null && !uId.isEmpty()) {
				int userCount = userFilterDao.getUserFilterCountByUserId(uId, userFilter.getFilterType()) + 1;
				userFilter.setFilterName( defaultName + userCount);
				int nameCount = userFilterDao.getUserFilterNameCount(uId, userFilter.getFilterName(), userFilter.getFilterType());
				while (nameCount > 0) {
					userCount = userCount + 1;
					userFilter.setFilterName(defaultName + userCount);
					nameCount = userFilterDao.getUserFilterNameCount(uId, userFilter.getFilterName(), userFilter.getFilterType());
				}
				UserFilterVO filterVO = userFilterDao.getUserFilterByFilterDetails(uId,
						userFilter.getFilter(),
						userFilter.getFilterType(),
						userFilter.getSortingKey(),
						userFilter.getLanguages()
						);
				if (filterVO != null) {
					userFilterJsonObject.set("filterName", filterVO.getFilterName());
					userFilterJsonObject.set("status", true);
				} else {
					response = userFilterDao.createUserFilter(userFilter);
					userFilterJsonObject.set("filterName", userFilter.getFilterName());
					userFilterJsonObject.set("status", true);
				}
				userFilterJsonObject.set("sortingKey", userFilter.getSortingKey());
				response = userFilterJsonObject.toString();
				}else{
					userFilterJsonObject.set("filterName", "My Collection");
					userFilterJsonObject.set("status", true);
					userFilterJsonObject.set("sortingKey", userFilter.getSortingKey());
					response = userFilterJsonObject.toString();
				}
			} else {
				userFilterJsonObject.set("status", false);
				userFilterJsonObject.set("sortingKey", userFilter.getSortingKey());
				response = userFilterJsonObject.toString();
			}
		} catch (Exception e) {
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		}
		return response;
	}

	@Override
	public String getFilters() {
		return AppgridHelper.appGridMetadata.get("filter").toString();
	}
	
	@SuppressWarnings("unchecked")
	public JsonArray getSortingOptionByFilterType(String filterType) {
		JsonArray asArray = null;
		filterType = filterType.equalsIgnoreCase("movie") ? "movie" : filterType.equalsIgnoreCase("tvshows") || filterType.equalsIgnoreCase("series") ? "tvshows" : "shorts";
		JsonArray filterJsonArray =  AppgridHelper.appGridMetadata.get("filter").asObject().get(filterType).asArray();
		JsonObject jsonObject = null;
		for (int i = 0; i < filterJsonArray.size(); i++) {
			jsonObject = filterJsonArray.get(i).asObject();
			if(jsonObject.get("label").asString().equals("Sort By")){
				asArray = jsonObject.get("category").asArray();
			break;	
			}
			
		}
		return asArray;
	}


	@SuppressWarnings("static-access")
	@Override
	public String getFilterContent(String uid, String filter, String byLanguages, String programType, String sortingKey, String totalPageSize, String pageSize, String pageNumber) {
		String baseURL =  AppgridHelper.mpxFeedData.get("content_filter").asString();
		String response = "";
		JsonObject responseJsonObject = null;
		try {
			
			response = ContentProviderUtil.createPagination(totalPageSize, pageSize, pageNumber, messageSource
					.getMessage(CPConstants.WYNKSTUDIO_MPX_PAGINATION_DEFAULT_START, null, "", Locale.ENGLISH),
					messageSource.getMessage(CPConstants.WYNKSTUDIO_MPX_PAGINATION_DEFAULT_END, null, "",
							Locale.ENGLISH));
			String[] range = response != null ? !response.isEmpty() ? response.trim().split("_") : null : null;
			if (range != null && range.length > 0) {
				baseURL = baseURL.replace("{0}",sortingKey != null && !sortingKey.isEmpty()&& sortingKey.equals(AppgridHelper.defaultsorting) ? "0" : Integer.parseInt(range[0]) > 10000 ? "910" : range[0]);
				baseURL = baseURL.replace("{1}", sortingKey != null && !sortingKey.isEmpty()&& sortingKey.equals(AppgridHelper.defaultsorting) ? "10000" : Integer.parseInt(range[1]) > 10000 ? "1000" : range[1]);
				filter = filter != null ? filter.replace(",", "|") : ""; 
				baseURL = baseURL.replace("{3}", programType);
				baseURL = setSortingOrder(baseURL, sortingKey);
				byLanguages = byLanguages != null && !byLanguages.isEmpty() ? byLanguages.replace(",", "|") : "";
				baseURL = baseURL.replace("{5}",  byLanguages != null ? byLanguages : "");
				if(filter.contains("premium")){
					filter = filter.replace("|premium", "");
					filter = filter.replace("premium", "");
					filter = getFilterByCategoryWise(filter);
					baseURL = baseURL.replace("{2}",  URLEncoder.encode(filter , "UTF-8"));
					response = Util.getCallWithoutRest(baseURL);
					responseJsonObject = new JsonObject().readFrom(response);
				}else{
					filter =  filter.replace("|free", "");
					filter =  filter.replace("free", "");
					String url = baseURL + "&byCustomValue={isFree}{true}";
					filter = getFilterByCategoryWise(filter);
					url = url.replace("{2}",  URLEncoder.encode(filter , "UTF-8"));
					response = Util.getCallWithoutRest(url);
						if(uid != null && !uid.isEmpty()){
							response = getLitePackEntries(uid, filter, baseURL, response);
						}				
						responseJsonObject = JsonObject.readFrom(response);
				}
				if(sortingKey != null && !sortingKey.isEmpty()&& sortingKey.equals(AppgridHelper.defaultsorting)){
					responseJsonObject.set("cp_sorting", sortingKey);
					responseJsonObject.set("totalResults", 10);
					response = responseJsonObject.toString();
				}
				response = ContentProviderHelper.createJsonResponse(response);
				responseJsonObject = JsonObject.readFrom(response);
				responseJsonObject.set("sorting_options", getSortingOptionByFilterType(programType));
				response = responseJsonObject.toString();
			} else {
				response = messageSource
						.getMessage(CPConstants.WYNKSTUDIO_MPX_LIMIT_OVERFLOW, null, "", Locale.ENGLISH);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return response;
	}

	private String getLitePackEntries(String uid, String filter,String baseURL, String response)
			throws ParseException, UnsupportedEncodingException {
		JSONParser parser = new JSONParser();
		ProductVO productByUserWithProductId = productDao.getProductByUserWithProductId( AppgridHelper.appGridMetadata.get("hooq_bytes_product").asString() , uid);
		List<ProductVO> hooqActiveProductByUserId = productDao.getProductsByUserId(uid);
		if(filter.toUpperCase().contains("SINGTEL") && filter.toUpperCase().contains("EROSNOW")){
			baseURL = baseURL.replace("{2}",  URLEncoder.encode(filter , "UTF-8"));
			if(productByUserWithProductId != null){
				response = getAllPlayableContent( baseURL + "&byCustomValue={pack}{lite}", parser, response);
			}
			if(hooqActiveProductByUserId != null && hooqActiveProductByUserId.size() > 0){
				response = getAllPlayableContent(baseURL + "&byCustomValue={isFree}{false}", parser, response);
			}
		}else{
		if(filter.toUpperCase().contains("SINGTEL")){
			baseURL = baseURL.replace("{2}",  URLEncoder.encode(filter , "UTF-8"));
			if(productByUserWithProductId != null){
				response = getAllPlayableContent(baseURL + "&byCustomValue={pack}{lite}", parser, response);
			}else if(hooqActiveProductByUserId != null && hooqActiveProductByUserId.size() > 0){
				for (ProductVO productVO : hooqActiveProductByUserId) {
					if(productVO.getCpId().contentEquals("SINGTEL")){
						filter = "&byCustomValue={isFree}{false}";
						response = getAllPlayableContent(baseURL + filter, parser, response);
						break;
					}	
				}
				}
		}else {
//			if(filter.toUpperCase().contains("EROSNOW")){
			if(hooqActiveProductByUserId != null && hooqActiveProductByUserId.size() > 0){
				for (ProductVO productVO : hooqActiveProductByUserId) {
					if(!filter.toUpperCase().contains("SINGTEL") && productVO.getCpId().contentEquals("SINGTEL")){
						filter = filter.endsWith(",") || filter.endsWith("|") ? filter : filter +",";
						filter = filter + "singtel" + "|";
					}
					if(!filter.toUpperCase().contains("EROSNOW") && productVO.getCpId().contentEquals("EROSNOW")){
						filter = filter.endsWith(",") || filter.endsWith("|") ? filter : filter +",";
						filter = filter + "erosnow" + "|";
					}
				}
					filter = filter.endsWith(",") || filter.endsWith("|") ? filter.substring(0, filter.length()-1) : filter;
					baseURL = baseURL.replace("{2}",  URLEncoder.encode(filter , "UTF-8"));
				response = getAllPlayableContent(baseURL + "&byCustomValue={isFree}{false}", parser, response);
			}
		}
		}
		
		return response;
	}

	private String getAllPlayableContent(String baseURL, JSONParser parser, String response) throws UnsupportedEncodingException, ParseException {
		JSONObject allFreeJsonObject = (JSONObject) parser.parse(response);
		JSONArray allFreeEntries = (JSONArray) allFreeJsonObject.get("entries");
		response = Util.getCallWithoutRest(baseURL);
		JSONObject liteJsonObject = (JSONObject) parser.parse(response);
		JSONArray entries = (JSONArray) liteJsonObject.get("entries");
		allFreeEntries.addAll(entries);
		allFreeJsonObject.put("entries", allFreeEntries);
		allFreeJsonObject.put("entryCount", allFreeEntries.size());
		response = allFreeJsonObject.toJSONString();
		return response;
	}

	private String setSortingOrder(String baseURL, String sortingKey) {
		switch (sortingKey.toLowerCase()) {
		case "year":
			baseURL = baseURL.replace("{4}", sortingKey + "|desc");
			break;
		case "added":
			baseURL = baseURL.replace("{4}", sortingKey + "|desc");
			break;
		case "rating":
			baseURL = baseURL.replace("{4}", ":imdbRating|desc");
			
			break;
		default:
			baseURL = baseURL.replace("&sort={4}", "");
			break;
		}
		return baseURL;
	}
	public UserFilterVO getRandomList(List<UserFilterVO> userFilterVOs) {

	    int index = random.nextInt(userFilterVOs.size());
	    return userFilterVOs.get(index);

	}
	public String getFiltersByUid(String uId, String count, String home) {
		String response = "";
		ObjectMapper mapper = new ObjectMapper();
		List<UserFilterVO> userFilterVOs = new ArrayList<>();
		HashMap<String, UserFilterVO> resultMap = new HashMap<>();
		UserFilterVO randomListItem = null;
		try {
			List<UserFilterVO> filterVOs = userFilterDao.getUserFilterUId(uId);
			if (count != null && !count.isEmpty() && filterVOs != null && filterVOs.size() > 0) {
				if (home != null && !home.isEmpty()) {
					List<UserFilterVO> userFiltersByCount = getUserFiltersByCount(filterVOs,
							Integer.parseInt(count));
					if (Boolean.parseBoolean(home)) {
						if (userFiltersByCount.size() > 0 && userFiltersByCount.size() > Integer.parseInt(count)) {
							for (int i = 0; i < userFiltersByCount.size(); i++) {
								randomListItem = getRandomList(userFiltersByCount);
								resultMap.put(randomListItem.getFilterName(), randomListItem);
								if(resultMap.size() == Integer.parseInt(count)){
									for (Map.Entry<String, UserFilterVO> entry : resultMap.entrySet()) {
										userFilterVOs.add(entry.getValue());
									}
									break;
								}
							}
						}else{
							userFilterVOs = userFiltersByCount;
						}
					} else {
						if (userFiltersByCount.size() > 0 && userFiltersByCount.size() > Integer.parseInt(count)) {
							for (int i = 0; i < userFiltersByCount.size(); i++) {
								userFilterVOs.add(userFiltersByCount.get(i));
								if(userFilterVOs.size() == Integer.parseInt(count)){
									break;
								}
							}
						} else {
							userFilterVOs = userFiltersByCount;
						}
					}
				}
			}else{
				userFilterVOs = filterVOs;
			}
			Object json = mapper.readValue(
					mapper.writeValueAsString(userFilterVOs), Object.class);
			response = mapper.writerWithDefaultPrettyPrinter()
				     .writeValueAsString(json);
		} catch (HttpClientErrorException e) {
			log.error("Error while making external API call,  " + uId + ":", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (JsonParseException e) {
			log.error("JSON Parse Error, " + uId + ":", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e, "Some error occured!");
		} catch (JsonMappingException e) {
			log.error("Json Mapping Error,  " + uId + ":", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e, "Some error occured!");
		} catch (JsonProcessingException e) {
			log.error("Json Processing Error, " + uId + ":", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e, "Some error occured!");
		} catch (IOException e) {
			log.error("IO Error, " + uId + ":", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e, "Some error occured!");
		} catch (BusinessApplicationException e) {
			log.error("User Not Found-, line 149 - " + uId + ":", e);
			throw new BusinessApplicationException(HttpStatus.NOT_FOUND.value(), e, "User Not Found!");
		} catch (Exception e) {
			log.error("Get Uer Profile Error, " + uId + ":", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e, "Some error occured!");
		}
		return response;
	}
	@Override
	public String deleteFilterByUid(String uId, String filterName) {
		String response = "";
		response = userFilterDao.deleteUserFilter(uId, filterName);
		return response;
	}

	@Override
	public String updateFilterByUid(String id, String userInfoJson) {
		String response = "";
		try {
			JsonObject userFilterJsonObject = JsonObject.readFrom(userInfoJson);
			if(id != null && !id.isEmpty()){
				response = userFilterDao.updateUserFilter(id, userFilterJsonObject.get("newName").asString(), userFilterJsonObject.get("oldName").asString());
			}else{
				response = "Updated successfully";
			}
			userFilterJsonObject.set("status", response);
			response = userFilterJsonObject.toString();
			
		} catch (Exception e) {
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Duplicate name");
		}
		return response;
	}

	public UserFilter createUserFilter(String uId, JsonObject userJson) {
		UserFilter userFilter = new UserFilter();
		UserProfile userProfile = new UserProfile();
		userProfile.setUserId(uId);
		userFilter.setUserProfile(userProfile);
		userFilter.setFilter(userJson.get("filter").asString());
		userFilter.setFilterType(userJson.get("filterType").asString());
			userFilter.setSortingKey(userJson.get("sortingKey") != null && !userJson.get("sortingKey").isNull() && !userJson.get("sortingKey").asString().isEmpty() ? userJson.get("sortingKey").asString() : AppgridHelper.defaultsorting);
			userFilter.setLanguages(userJson.get("languages") != null && !userJson.get("languages").isNull() && !userJson.get("languages").asString().isEmpty()? userJson.get("languages").asString() : "");
		return userFilter;

	}
	
	@SuppressWarnings("unchecked")
	public List<UserFilterVO> getUserFiltersByCount(List<UserFilterVO> filterVOs, int count  ) {
		int lmit = Integer.parseInt(AppgridHelper.appGridMetadata.get("home_page_filter_limit").asString());
		List<UserFilterVO> userFilterVOs = new ArrayList<>();
		final ExecutorService es = Executors.newFixedThreadPool(filterVOs.size());
		try {
		List<Future<Integer>> futures = new ArrayList<Future<Integer>>();
		final int typesLength = filterVOs.size();
		for (int i = 0; i < typesLength; i++) {
			final int j = i;
			final UserFilterVO userFilterVO = filterVOs.get(j);
			futures.add(es.submit(new Callable() {
				public Object call() throws Exception {
					return getUserFilterListByContent(userFilterVO, es);
				}
			}));
		}
		int k =0;
		for (Future future : futures) {
			Future<Integer> futureResponse = (Future<Integer>) future.get();
			if (futureResponse != null && futureResponse.get() != null && futureResponse.get() != 0 && futureResponse.get() >= lmit) {
				userFilterVOs.add(filterVOs.get(k));
			}
			k++;
		}
		es.shutdown();
		} catch (Exception e) {
			es.shutdown();
			// TODO: handle exception
		}
		return userFilterVOs;
	}
	
	@Async
	private Future<Integer> getUserFilterListByContent(UserFilterVO userFilterVO, ExecutorService es) {
		int size = 0;
		try {
			size = getFilterContentCount(userFilterVO.getFilter(), userFilterVO.getFilterType(), userFilterVO.getSortingKey(), "", "", "", userFilterVO.getLanguages(), userFilterVO.getuId());
		} catch (HttpClientErrorException e) {
			log.error("Search Impl- Http Exception- Line 166: ", e);
			es.shutdown();
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		}

		return new AsyncResult<Integer>(size);
	}

	
private boolean getFilterContentEmptyOrNot(String filter, String programType, String sortingKey, String totalPageSize, String pageSize, String pageNumber,String byLanguages, String uid) {
		
		boolean result = false;
		try {
			int count = getFilterContentCount(filter, programType, sortingKey, totalPageSize, pageSize, pageNumber, byLanguages, uid);
				if(count > 0){
					result = true;
				}
		} catch (HttpClientErrorException e) {
			log.error("Error, line 522 -", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (Exception e) {
			log.error("Error, line 525 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occurred!");
		}
		return result;
	}


private int getFilterContentCount(String filter, String programType, String sortingKey, String totalPageSize, String pageSize, String pageNumber, String byLanguages, String uid) {
	
	String baseURL =  AppgridHelper.mpxFeedData.get("content_filter").asString() + "&fields=title";
	int count = 0;
	JsonObject responseJsonObject = null;
	String response = ContentProviderUtil.createPagination(totalPageSize, pageSize, pageNumber, messageSource
				.getMessage(CPConstants.WYNKSTUDIO_MPX_PAGINATION_DEFAULT_START, null, "", Locale.ENGLISH),
				messageSource.getMessage(CPConstants.WYNKSTUDIO_MPX_PAGINATION_DEFAULT_END, null, "",
						Locale.ENGLISH));
	try {
		
	
		String[] range = response != null ? !response.isEmpty() ? response.trim().split("_") : null : null;
			baseURL = baseURL.replace("{0}", Integer.parseInt(range[0]) > 10000 ? "910" : range[0]);
			baseURL = baseURL.replace("{1}", Integer.parseInt(range[1]) > 10000 ? "1000" : range[1]);
			filter = filter != null ? filter.replace(",", "|") : ""; 
			filter = filter != null ? filter.replace(",", "|") : ""; 
			baseURL = baseURL.replace("{3}", programType);
			baseURL = setSortingOrder(baseURL, sortingKey);
			byLanguages = byLanguages != null && !byLanguages.isEmpty() ? byLanguages.replace(",", "|") : "";
			baseURL = baseURL.replace("{5}",  byLanguages != null ? byLanguages : "");
			if(filter.contains("premium")){
				filter = filter.replace("|premium", "");
				filter = filter.replace("premium", "");
				filter = getFilterByCategoryWise(filter);
				baseURL = baseURL.replace("{2}",  URLEncoder.encode(filter , "UTF-8"));
				response = Util.getCallWithoutRest(baseURL);
				responseJsonObject = new JsonObject().readFrom(response);
			}else{
				filter =  filter.replace("|free", "");
				filter =  filter.replace("free", "");
				String url = baseURL + "&byCustomValue={isFree}{true}";
				filter = getFilterByCategoryWise(filter);
				url = url.replace("{2}",  URLEncoder.encode(filter , "UTF-8"));
				response = Util.getCallWithoutRest(url);
					if(uid != null && !uid.isEmpty()){
						response = getLitePackEntries(uid, filter, baseURL, response);
					}				
					responseJsonObject = JsonObject.readFrom(response);
			}
//			String url = baseURL.substring(0, baseURL.indexOf("?"));
//			String parameter = baseURL.substring(baseURL.indexOf("?")+1, baseURL.length());
//			parameter = URLEncoder.encode(parameter, "UTF-8");
//			response = Util.getCallWithoutRest(baseURL);
//			JsonObject responseJsonObject = new JsonObject().readFrom(response);
			count = responseJsonObject.get("entryCount").asInt();
	} catch (Exception e) {
		// TODO: handle exception
	}
	return count;
}

	private String getFilterByCategoryWise(String filter) {
		String finalFilter = "";
		List<String> list = new ArrayList<>();
		
		HashMap<String, List<String>> cateogryMapping = new HashMap<>();
//		if(AppgridHelper.filterMap == null){
//			AppgridHelper.setFilterMapping();
//		}
		List<String> names = AppgridHelper.filterCategories.names();
		for (String name : names) {
			cateogryMapping.put(name, null);
		}
		String cateogry = null;
		filter = filter.replace("|", "~");
		String[] filters = filter.length() > 3 ? filter.split("~") : null;
		if (filters != null && filters.length > 0) {
			for (int i = 0; i < filters.length; i++) {
				cateogry = AppgridHelper.filterMap.get(filters[i]);
				List<String> addList = cateogryMapping.get(cateogry);
				if(addList == null){
					addList =  new ArrayList<>() ;
				}
				addList.add(filters[i]) ;
				cateogryMapping.put(cateogry,addList );
			}
			for (Map.Entry me : cateogryMapping.entrySet()) {
				list = (List<String>) me.getValue();
				if (list != null) {
					for (String tag : list) {
						finalFilter = finalFilter + tag + "|";
					}
				finalFilter = finalFilter.endsWith("|") ? finalFilter.substring(0, finalFilter.length() - 1)
						: finalFilter;
				finalFilter = finalFilter + ",";
				}
			}

		}
		return finalFilter.endsWith("|") || finalFilter.endsWith(",")
				? finalFilter.substring(0, finalFilter.length() - 1) : finalFilter;
	}

}
