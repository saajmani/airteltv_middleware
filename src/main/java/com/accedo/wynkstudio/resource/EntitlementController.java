package com.accedo.wynkstudio.resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.accedo.wynkstudio.delegate.EntitlementDelegate;

@RestController
@RequestMapping({ "v0.11/", "v1/", "v0.12/", "v0.13/", "v0.14/", "v0.15/", "v0.16/", "v0.17/" , "v0.18/"})
public class EntitlementController {
	
	@Autowired
	EntitlementDelegate entitlementDelegate;
	
	/* Subscription Check */

	@RequestMapping(value = "/entitlement/status", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getEntitlementStatus(@RequestParam("contentId") String contentId,
			@RequestParam("uid") String uid, @RequestParam("cpId") String cpId, @RequestBody String userInfoJson,
			HttpServletRequest request, HttpServletResponse response) {
		String responseString = entitlementDelegate.checkEntitlementStatus(contentId, cpId, uid, userInfoJson);
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}
	
	/* Subscription Check */

	@RequestMapping(value = "/bundle/add", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public @ResponseBody String setBundleCounter(@RequestParam("contentId") String contentId,
			@RequestParam("uid") String uid, @RequestParam("productId") String productId,
			HttpServletRequest request, HttpServletResponse response) {
		String responseString = entitlementDelegate.addMediaToBundle(uid, productId, contentId);
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}

}
