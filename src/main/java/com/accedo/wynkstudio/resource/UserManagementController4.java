package com.accedo.wynkstudio.resource;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.accedo.wynkstudio.delegate.UserDelegate;

@RestController
@RequestMapping({"v0.16/", "v0.17/", "v0.18/"})
public class UserManagementController4 extends UserManagementController3{
	
	@Autowired
	UserDelegate userDelegate;

	@Autowired
	private MessageSource messageSource;


	/* Get User Rails API Without  getting gifit*/

	@Override
    @RequestMapping(value = "/account/rails", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
        public @ResponseBody String getRails(@RequestParam(value = "uid", required = false) String userId,
                        @RequestParam(value = "uId", required = false) String uId,@RequestParam(value = "token", required = false) String token,
                        @RequestParam(value = "airtel", required = false) Boolean airtel,
                        @RequestParam(value = "deviceId", required = false) String deviceId,
                        @RequestParam(value = "os", required = false, defaultValue = "") String deviceOs,
                        @RequestParam(value = "appVersion", required = false, defaultValue = "") String appVersion,
                        HttpServletResponse response) {
                userId = (userId == null || userId.isEmpty()) ? uId : userId;
                token = (token == null || token.isEmpty()) ? "" : token;
                airtel = (airtel == null) ? false : airtel;
                deviceId = (deviceId == null) ? "" : deviceId;
                String responseString = userDelegate.getCards(userId, token, airtel, "", deviceId, false, deviceOs, appVersion);
                response.setHeader("Cache-Control", "no-cache");
                return responseString;
        }
        
        /* 
        Adding a new method to support iOS. need to be removed after resolving mapping issue.
        */
        @RequestMapping(value = "/account/userprofile", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getUserProfile(@RequestParam(value = "uid", required = false) String userId,
			@RequestParam(value = "uId", required = false) String uId,
			@RequestParam(value = "token", required = false) String token, HttpServletRequest request,
			@RequestParam(value = "deviceId", required = false) String deviceId,
                        @RequestParam(value = "os", required = false, defaultValue = "") String deviceOs,
                        @RequestParam(value = "appVersion", required = false, defaultValue = "") String appVersion,
			HttpServletResponse response) {
		userId = (userId == null || userId.isEmpty()) ? uId : userId;
		String responseString = userDelegate.getUserByIdWithVersion(userId, request.getSession().getServletContext().getRealPath("/"), token, deviceId, deviceOs, appVersion);
		
		JsonObject responseJson = JsonObject.readFrom(responseString);
		if(responseJson.get("statusCode") !=null && !responseJson.get("statusCode").isNull())
		{
			response.setStatus(HttpStatus.OK.value());
		}
		
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}	

}
