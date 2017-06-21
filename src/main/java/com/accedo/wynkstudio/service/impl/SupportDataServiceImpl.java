package com.accedo.wynkstudio.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

import com.accedo.wynkstudio.dao.BundleCounterDao;
import com.accedo.wynkstudio.dao.FavouriteDao;
import com.accedo.wynkstudio.dao.ProductDao;
import com.accedo.wynkstudio.dao.RecentDao;
import com.accedo.wynkstudio.dao.UserProfileDao;
import com.accedo.wynkstudio.exception.BusinessApplicationException;
import com.accedo.wynkstudio.helper.AppgridHelper;
import com.accedo.wynkstudio.service.SupportDataService;
import com.accedo.wynkstudio.util.Util;
import com.accedo.wynkstudio.vo.BundleCounterVO;
import com.accedo.wynkstudio.vo.FavouriteVO;
import com.accedo.wynkstudio.vo.ProductVO;
import com.accedo.wynkstudio.vo.RecentVO;
import com.accedo.wynkstudio.vo.UserProfileVO;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional(readOnly = true)
public class SupportDataServiceImpl implements SupportDataService {

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
	private BundleCounterDao bundleCounterDao;

	private HttpHeaders headers;
	final Logger log = LoggerFactory.getLogger(this.getClass());

	@PostConstruct
	public void init() {
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	}

	@Override
	public String getUserById(String userId) {
		String response = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			UserProfileVO userProfileVO = userProfileDao.getUserProfileByUserId(userId);
			Object json = mapper.readValue(mapper.writeValueAsString(userProfileVO), UserProfileVO.class);
			if (json != null) {
				response = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
				response = convertToAllUserInfo(response);
			} else {
				response = "{\"message\":\"User Not Found!\", \"status\": 404}";
			}
		} catch (HttpClientErrorException e) {
			log.error("Error, line 88 -", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (JsonParseException e) {
			log.error("Error, line 90 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		} catch (JsonMappingException e) {
			log.error("Error, line 94 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		} catch (JsonProcessingException e) {
			log.error("Error, line 97 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		} catch (IOException e) {
			log.error("Error, line 100 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		} catch (Exception e) {
			log.error("Error, line 103 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return response;
	}

	@Override
	public String getUserBasicInfo(String userId) {
		String response = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			UserProfileVO userProfileVO = userProfileDao.getUserProfileByUserId(userId);
			Object json = mapper.readValue(mapper.writeValueAsString(userProfileVO), UserProfileVO.class);
			if (json != null) {
				response = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
				response = convertToBasicUserInfo(response);
			} else {
				response = "{\"message\":\"User Not Found!\", \"status\": 404}";
			}
		} catch (HttpClientErrorException e) {
			log.error("Error, line 123 -", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (JsonParseException e) {
			log.error("Error, line 126 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		} catch (JsonMappingException e) {
			log.error("Error, line 129 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		} catch (JsonProcessingException e) {
			log.error("Error, line 132 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		} catch (IOException e) {
			log.error("Error, line 135 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		} catch (Exception e) {
			log.error("Error, line 138 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return response;
	}

	@Override
	public String getUserFavourites(String userId) {
		String response = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			List<FavouriteVO> favouritesVOs = favouriteDao.getFavouriteListByUserId(userId);
			Object json = mapper.readValue(mapper.writeValueAsString(favouritesVOs), Object.class);
			response = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
			response = convertToFavourites(JsonArray.readFrom(response)).toString();

		} catch (HttpClientErrorException e) {
			log.error("Error, line 155 -", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (JsonParseException e) {
			log.error("Error, line 158 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		} catch (JsonMappingException e) {
			log.error("Error, line 161 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		} catch (JsonProcessingException e) {
			log.error("Error, line 164 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		} catch (IOException e) {
			log.error("Error, line 167 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		} catch (Exception e) {
			log.error("Error, line 170 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return response;
	}

	@Override
	public String getUserRecent(String userId) {
		String response = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			List<RecentVO> recentVOs = recentDao.getRecentListUserId(userId);
			Object json = mapper.readValue(mapper.writeValueAsString(recentVOs), Object.class);
			response = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
			response = convertToRecent(JsonArray.readFrom(response)).toString();
		} catch (HttpClientErrorException e) {
			log.error("Error, line 186 -", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (JsonParseException e) {
			log.error("Error, line 189 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		} catch (JsonMappingException e) {
			log.error("Error, line 192 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		} catch (JsonProcessingException e) {
			log.error("Error, line 195 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		} catch (IOException e) {
			log.error("Error, line 198 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		} catch (Exception e) {
			log.error("Error, line 201 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return response;
	}

	@Override
	public String getUserPacks(String userId) {
		String response = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			List<ProductVO> productVOs = productDao.getProductsByUserId(userId);
			Object json = mapper.readValue(mapper.writeValueAsString(productVOs), Object.class);
			response = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
			response = convertToPacks(JsonArray.readFrom(response)).toString();
		} catch (HttpClientErrorException e) {
			log.error("Error, line 217 -", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (JsonParseException e) {
			log.error("Error, line 220 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		} catch (JsonMappingException e) {
			log.error("Error, line 222 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		} catch (JsonProcessingException e) {
			log.error("Error, line 226 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		} catch (IOException e) {
			log.error("Error, line 229 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		} catch (Exception e) {
			log.error("Error, line 232 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return response;
	}

	@Override
	public String getUserBundles(String userId) {
		String response = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			List<BundleCounterVO> bundleCounterVOs = bundleCounterDao.getBundleCountersByUserId(userId);
			Object json = mapper.readValue(mapper.writeValueAsString(bundleCounterVOs), Object.class);
			response = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
			response = convertToBundles(JsonArray.readFrom(response)).toString();
		} catch (HttpClientErrorException e) {
			log.error("Error, line 248 -", e);
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		} catch (JsonParseException e) {
			log.error("Error, line 251 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		} catch (JsonMappingException e) {
			log.error("Error, line 254 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		} catch (JsonProcessingException e) {
			log.error("Error, line 257 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		} catch (IOException e) {
			log.error("Error, line 260 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		} catch (Exception e) {
			log.error("Error, line 263 -", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
		return response;
	}

	private String convertToAllUserInfo(String responseJson) {
		JsonObject responseJsonObject = JsonObject.readFrom(responseJson);
		JsonObject userInfoAllObject = new JsonObject();
		userInfoAllObject.add("name", responseJsonObject.get("name"));
		userInfoAllObject.add("dob", responseJsonObject.get("dob"));
		userInfoAllObject.add("gender", responseJsonObject.get("gender"));
		userInfoAllObject.add("email", responseJsonObject.get("email"));
		userInfoAllObject.add("bsbUid", responseJsonObject.get("userId"));
		userInfoAllObject
				.add("userFavourites", convertToFavourites(responseJsonObject.get("favoriteMovies").asArray()));
		userInfoAllObject
				.add("recentWatchList", convertToRecent(responseJsonObject.get("lastWatchedMovies").asArray()));
		userInfoAllObject
				.add("subscribedPacks", convertToPacks(responseJsonObject.get("subscribedChannels").asArray()));
		userInfoAllObject.add("bundlePackState", convertToBundles(responseJsonObject.get("bundleCounter").asArray()));
		return userInfoAllObject.toString();

	}

	private String convertToBasicUserInfo(String responseJson) {
		JsonObject responseJsonObject = JsonObject.readFrom(responseJson);
		JsonObject userInfoAllObject = new JsonObject();
		userInfoAllObject.add("name", responseJsonObject.get("name"));
		userInfoAllObject.add("dob", responseJsonObject.get("dob"));
		userInfoAllObject.add("gender", responseJsonObject.get("gender"));
		userInfoAllObject.add("email", responseJsonObject.get("email"));
		userInfoAllObject.add("bsbUid", responseJsonObject.get("userId"));
		return userInfoAllObject.toString();

	}

	private JsonArray convertToFavourites(JsonArray favouriteJson) {
		JsonArray responseArray = new JsonArray();
		JsonObject mpxFeedJson = AppgridHelper.mpxFeedData;
		HashMap<String, String> assetsMap = new HashMap<String, String>();
		String assetIds = "";
		String programsUrl = mpxFeedJson.get("programbyid").asString();
		for (JsonValue favourite : favouriteJson) {
			assetIds = assetIds + favourite.asObject().get("assetId").asString() + "|";
		}

		String response = Util.executeApiGetCall(programsUrl.replace("{0}", assetIds) + "&fields=guid,title");
		JsonArray mpxJson = JsonObject.readFrom(response).get("entries").asArray();
		for (JsonValue asset : mpxJson) {
			assetsMap.put(asset.asObject().get("guid").asString(), asset.asObject().get("title").asString());
		}

		for (JsonValue favourite : favouriteJson) {
			JsonObject favObject = new JsonObject();
			favObject.add("contentId", favourite.asObject().get("assetId").asString());
			favObject.add("cpId", favourite.asObject().get("cpToken").asString());
			favObject.add("title", assetsMap.get(favourite.asObject().get("assetId").asString()));
			responseArray.add(favObject);
		}

		return responseArray;
	}

	private JsonArray convertToRecent(JsonArray recentJson) {
		JsonArray responseArray = new JsonArray();
		JsonObject mpxFeedJson = AppgridHelper.mpxFeedData;
		HashMap<String, String> assetsMap = new HashMap<String, String>();
		String assetIds = "";
		String programsUrl = mpxFeedJson.get("programbyid").asString();
		for (JsonValue favourite : recentJson) {
			assetIds = assetIds + favourite.asObject().get("assetId").asString() + "|";
		}

		String response = Util.executeApiGetCall(programsUrl.replace("{0}", assetIds) + "&fields=guid,title");
		JsonArray mpxJson = JsonObject.readFrom(response).get("entries").asArray();
		for (JsonValue asset : mpxJson) {
			assetsMap.put(asset.asObject().get("guid").asString(), asset.asObject().get("title").asString());
		}

		for (JsonValue favourite : recentJson) {
			JsonObject favObject = new JsonObject();
			favObject.add("contentId", favourite.asObject().get("assetId").asString());
			favObject.add("cpId", favourite.asObject().get("cpToken").asString());
			favObject.add("title", assetsMap.get(favourite.asObject().get("assetId").asString()));
			favObject.add("lastViewedTime", favourite.asObject().get("lastWatchedTime").asString());
			responseArray.add(favObject);
		}

		return responseArray;
	}

	private JsonArray convertToPacks(JsonArray subscribedChannels) {
		JsonArray responseArray = new JsonArray();

		for (JsonValue favourite : subscribedChannels) {
			JsonObject favObject = new JsonObject();
			favObject.add("productId", favourite.asObject().get("productId").asString());
			favObject.add("cpId", favourite.asObject().get("cpId").asString());
			favObject.add("state", favourite.asObject().get("state").asString());
			favObject.add("allowPlayback", favourite.asObject().get("allowPlayback").asBoolean());
			favObject.add("active", favourite.asObject().get("active").asBoolean());
			
			if(favourite.asObject().get("active").asBoolean())
			{
				long cpVal = favourite.asObject().get("contentValidity").asLong() / 1000;
				favObject.add("cpValidity", String.valueOf(cpVal));
			}
			else
			{
				favObject.add("cpValidity", "N/A");
			}
			
			responseArray.add(favObject);
		}

		return responseArray;
	}

	private JsonArray convertToBundles(JsonArray bundleCounter) {
		JsonArray responseArray = new JsonArray();

		for (JsonValue favourite : bundleCounter) {
			JsonObject favObject = new JsonObject();
			favObject.add("productId", favourite.asObject().get("productId").asString());
			favObject.add("cpId", favourite.asObject().get("cpId").asString());
			favObject.add("playbackLimit", favourite.asObject().get("itemLimit").asInt());
			favObject.add("playbackCounter", favourite.asObject().get("counter").asInt());
			if (favourite.asObject().get("mediaList") != null && !favourite.asObject().get("mediaList").isNull()) {
				favObject.add("playedMedia", favourite.asObject().get("mediaList").asArray());
			} else {
				favObject.add("playedMedia", new JsonArray());
			}

			responseArray.add(favObject);
		}

		return responseArray;
	}

}
