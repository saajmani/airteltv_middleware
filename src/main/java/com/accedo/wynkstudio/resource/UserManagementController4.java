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
@RequestMapping({"v0.16/" })
public class UserManagementController4 extends UserManagementController3{
	
	@Autowired
	UserDelegate userDelegate;

	@Autowired
	private MessageSource messageSource;


	/* Get User Rails API Without  getting gifit*/

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
		String responseString = userDelegate.getCards(userId, token, airtel, "", deviceId, false);
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}

}
