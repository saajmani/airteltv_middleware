package com.accedo.wynkstudio.resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.accedo.wynkstudio.delegate.UserDelegate;
import com.eclipsesource.json.JsonObject;

@RestController
@RequestMapping({ "v0.11/", "v0.12/", "v1/" })
public class UserManagementController {

	@Autowired
	UserDelegate userDelegate;

	@Autowired
	private MessageSource messageSource;

	/* Metadata Updation */

	@RequestMapping(value = "/metadata", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String updateMetadata() {
		return userDelegate.getMetadata().toString();
	}
	
	@RequestMapping(value = "/metadata/refresh", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String refreshMetadata() {
		return userDelegate.refreshMetadata();
	}

	/* User Profile Creation */

	@RequestMapping(value = "/account/profile", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public @ResponseBody String setUser(@RequestParam(value="uid", required=false) String userId, @RequestParam(value="uId", required=false) String uId, @RequestBody String userInfoJson) {
		userId = (userId == null || userId.isEmpty() )? uId : userId;
		String responseString = userDelegate.createUserById(userId, userInfoJson);
		return responseString;
	}

	/* Get User Profile */

	@RequestMapping(value = "/account/profile", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getUser(@RequestParam(value = "uid", required = false) String userId,
			@RequestParam(value = "uId", required = false) String uId,
			@RequestParam(value = "token", required = false) String token, HttpServletRequest request,
			@RequestParam(value = "deviceId", required = false) String deviceId,
			HttpServletResponse response) {
		userId = (userId == null || userId.isEmpty()) ? uId : userId;
		String responseString = userDelegate.getUserById(userId, request.getSession().getServletContext().getRealPath("/"), token, deviceId);
		
		JsonObject responseJson = JsonObject.readFrom(responseString);
		if(responseJson.get("statusCode") !=null && !responseJson.get("statusCode").isNull())
		{
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}

	/* Update User Profile data for a particular dataKey */

	@RequestMapping(value = "/account/profile", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public @ResponseBody String updateUser(@RequestParam(value = "uid", required = false) String userId,
			@RequestParam(value = "uId", required = false) String uId, @RequestParam("dataKey") String dataKey,
			@RequestBody String userInfoJson) {
		userId = (userId == null || userId.isEmpty()) ? uId : userId;
		String responseString = userDelegate.updateUserById(userId, dataKey, userInfoJson);
		return responseString;
	}

	@RequestMapping(value = "/account/favorites", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public @ResponseBody String updateFavourite(@RequestParam(value="uid", required=false) String userId,  @RequestParam(value="uId", required=false) String uId,
			@RequestBody String userInfoJson) {
		userId = (userId == null || userId.isEmpty() )? uId : userId;
		String responseString = userDelegate.updateUserFavouriteList(userId, userInfoJson);

		return responseString;
	}

	@RequestMapping(value = "/account/recent", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public @ResponseBody String updateRecent(@RequestParam(value="uid", required=false) String userId,  @RequestParam(value="uId", required=false) String uId,
			@RequestBody String userInfoJson) {
		userId = (userId == null || userId.isEmpty() )? uId : userId;
		String responseString = userDelegate.updateUserRecentList(userId, userInfoJson);

		return responseString;
	}

	@RequestMapping(value = "/account/favorites", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
	public @ResponseBody String removeAppgridDataBykey(@RequestParam(value = "uid", required = false) String userId,
			@RequestParam(value = "uId", required = false) String uId, @RequestParam("assetId") String assetId) {
		userId = (userId == null || userId.isEmpty()) ? uId : userId;
		String dataKey = "favoriteMovies";
		String responseString = userDelegate.removeAppgridDataBykey(userId, dataKey, assetId);
		return responseString;
	}

	/* Appgrid Get Favourites list */

	@RequestMapping(value = "/account/favorites", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getFavourites(@RequestParam(value = "uid", required = false) String userId,
			@RequestParam(value = "uId", required = false) String uId,
			@RequestParam(value = "cpIds", required = false, defaultValue = "") String cpIds,
			HttpServletResponse response) {
		String dataKey = "";
		userId = (userId == null || userId.isEmpty()) ? uId : userId;
		String responseString = userDelegate.getFavourites(userId, dataKey, cpIds);
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}

	/* Appgrid Get Recent list */

	@RequestMapping(value = "/account/recent", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getRecent(@RequestParam(value = "uid", required = false) String userId,
			@RequestParam(value = "uId", required = false) String uId,
			@RequestParam(value = "cpIds", required = false, defaultValue = "") String cpIds,
			HttpServletResponse response) {
		String dataKey = "";
		userId = (userId == null || userId.isEmpty()) ? uId : userId;
		String responseString = userDelegate.getRecentList(userId, dataKey, cpIds);
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}

	/* Get MSP Downloads Window */

	@RequestMapping(value = "/msp/window", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getDownloadWindow(HttpServletResponse response) {
		String responseString = userDelegate.getMsp();
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}
	
	@RequestMapping(value = "/gift/info", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getGiftInfo(@RequestParam(value = "uid", required = false) String userId, HttpServletResponse response) {
		String responseString = userDelegate.getGiftInfo(userId);
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}


	/* MPX Trusted Auth SignIn */

	@RequestMapping(value = "/mpx/auth", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getUserTokenFromMPX(@RequestParam("userId") String userId,
			@RequestBody String userInfoJson, HttpServletRequest request, HttpServletResponse response) {
		String responseString = userDelegate.getUserTokenFromMPX(userId, request.getSession().getServletContext()
				.getRealPath("/"), userInfoJson, "");
		return responseString;
	}
	
	@RequestMapping(value = "/account/rails", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getRails(@RequestParam(value = "uid", required = false) String userId,
			@RequestParam(value = "uId", required = false) String uId,@RequestParam(value = "token", required = false) String token,
			@RequestParam(value = "airtel", required = false) Boolean airtel,
			@RequestParam(value = "deviceId", required = false) String deviceId,
			HttpServletResponse response) {
		userId = (userId == null || userId.isEmpty()) ? uId : userId;
		token = (token == null || token.isEmpty()) ? "" : token;
		airtel = (airtel == null) ? false : airtel;
		deviceId = (deviceId == null) ? "" : deviceId;
		String responseString = userDelegate.getRails(userId, token, airtel, "", deviceId, true);
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}

	@RequestMapping(value = "/account/recent", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
	public @ResponseBody String removeRecent(@RequestParam(value = "uid", required = false) String userId, @RequestParam("assetId") String assetId) {
		
		String responseString = userDelegate.removeRecent(userId, assetId);
		return responseString;
	}
	
}
