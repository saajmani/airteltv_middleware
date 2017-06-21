package com.accedo.wynkstudio.resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.accedo.wynkstudio.delegate.UserDelegate;
import com.eclipsesource.json.JsonObject;

@RestController
@RequestMapping({ "v0.13/", "v0.14/"})
public class UserManagementController2 extends UserManagementController{

	@Autowired
	UserDelegate userDelegate;

	@Autowired
	private MessageSource messageSource;


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
			response.setStatus(HttpStatus.OK.value());
		}
		
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}

}
