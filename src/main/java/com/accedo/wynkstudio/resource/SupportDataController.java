package com.accedo.wynkstudio.resource;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.accedo.wynkstudio.delegate.SupportDataDelegate;
import com.accedo.wynkstudio.exception.BusinessApplicationException;
import com.eclipsesource.json.JsonObject;

@RestController
@RequestMapping({ "v0.11/", "v1/", "v0.12/", "v0.13/", "v0.14/", "v0.15/", "v0.16/" })
public class SupportDataController {
	
	@Autowired
	SupportDataDelegate supportDataDelegate;

	@Autowired
	private MessageSource messageSource;
	
	/* Payment Success */

	@RequestMapping(value = "/user/{uid}/all", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getUserAllInfo(@PathVariable("uid") String uid, HttpServletResponse response) {
		String responseString = supportDataDelegate.getUserById(uid);
		JsonObject jsonObject = JsonObject.readFrom(responseString);
		if(jsonObject.get("status") != null && !jsonObject.get("status").isNull())
		{
			response.setStatus(jsonObject.get("status").asInt());
		}
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}
	
	@RequestMapping(value = "/user/{uid}/info", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getUserBasicInfo(@PathVariable("uid") String uid, HttpServletResponse response) {
		String responseString = supportDataDelegate.getUserBasicInfo(uid);
		JsonObject jsonObject = JsonObject.readFrom(responseString);
		if(jsonObject.get("status") != null && !jsonObject.get("status").isNull())
		{
			response.setStatus(jsonObject.get("status").asInt());
		}
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}
	
	@RequestMapping(value = "/user/{uid}/favourites", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getUserFavourites(@PathVariable("uid") String uid, HttpServletResponse response) {
		String responseString = supportDataDelegate.getUserFavourites(uid);
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}
	
	@RequestMapping(value = "/user/{uid}/recent", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getUserRecent(@PathVariable("uid") String uid, HttpServletResponse response) {
		String responseString = supportDataDelegate.getUserRecent(uid);
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}
	
	@RequestMapping(value = "/user/{uid}/packs", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getUserPacks(@PathVariable("uid") String uid, HttpServletResponse response) {
		String responseString = supportDataDelegate.getUserPacks(uid);
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}
	
	@RequestMapping(value = "/user/{uid}/bundles", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getUserBundles(@PathVariable("uid") String uid, HttpServletResponse response) {
		String responseString = supportDataDelegate.getUserBundles(uid);
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}
}
