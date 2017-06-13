package com.accedo.wynkstudio.resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.accedo.wynkstudio.delegate.SubscriptionDelegate;

@RestController
@RequestMapping({ "v0.11/", "v1/", "v0.12/", "v0.13/", "v0.14/", "v0.15/", "v0.16/", "v0.17/", "v0.18/"})
public class SubscriptionManagementController {
	
	final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	SubscriptionDelegate subscriptionDelegate;

	@Autowired
	private MessageSource messageSource;

	/* Payment Success */

	@RequestMapping(value = "/subscribe", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public @ResponseBody String setProductEntitlement(@RequestParam(value = "cpId", required = false) String cpId,
			@RequestParam("uid") String uid, @RequestParam("product") String product, @RequestBody String userInfoJson,
			HttpServletRequest request, HttpServletResponse response) {
		String responseString = subscriptionDelegate.setEntitlementsForUser(uid, product, request.getSession()
				.getServletContext().getRealPath("/"), userInfoJson, cpId);
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}
	
	@RequestMapping(value = "/purchase", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public @ResponseBody String cpPurchase(@RequestParam(value = "cpId", required = false) String cpId,
			@RequestParam("uid") String uid, @RequestParam("product") String product,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("BSB Calls Provisioning API for uid :" + uid +", product:" + product + ", CP:" + cpId);
		String responseString = subscriptionDelegate.setEntitlementsByBsb(uid, product, request.getSession()
				.getServletContext().getRealPath("/"), "{}", cpId);
		log.info("Response for Provisioning API : " + responseString + "for uid :" + uid +", product:" + product + ", CP:" + cpId);
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}

	/* Unsubscribe */

	@RequestMapping(value = "/unsubscribe", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public @ResponseBody String unsubscribe(@RequestParam(value = "cpId", required = false) String cpId,
			@RequestParam("uid") String uid, @RequestParam("product") String product, @RequestBody String userInfoJson,
			HttpServletRequest request, HttpServletResponse response) {
		String responseString = subscriptionDelegate.unsubscribeUser(uid, product, request.getSession().getServletContext()
				.getRealPath("/"), userInfoJson, cpId);
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}

	/* Packstatus */

	@RequestMapping(value = "/packstatus", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getPackStatus(@RequestParam("uid") String uid,
			@RequestParam(value = "cpIds", required = false, defaultValue = "") String cpIds,
			@RequestBody String userInfoJson, HttpServletRequest request, HttpServletResponse response) {
		String responseString = subscriptionDelegate.getPackStatus(uid, userInfoJson, cpIds);
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}
	
	/* Provision  */

	@RequestMapping(value = "/activate", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String activateProduct(@RequestParam("uid") String uid,
                        @RequestParam(value = "token", required = false, defaultValue = "") String token,
			@RequestParam("productId") String productId, 
                        @RequestParam(value = "cpId", required = false, defaultValue = "") String cpId,
                        @RequestParam(value = "platform", required = false, defaultValue = "") String platform,
                        @RequestParam(value = "deviceId", required = false, defaultValue = "") String deviceId,
                        @RequestParam(value = "os", required = false, defaultValue = "") String deviceOs,
                        @RequestParam(value = "appVersion", required = false, defaultValue = "") String appVersion,
			HttpServletRequest request, HttpServletResponse response) {
		String responseString = subscriptionDelegate.activateProduct(uid, token, productId, cpId, deviceId, platform, deviceOs, appVersion);
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}

}
