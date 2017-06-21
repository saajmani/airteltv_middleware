package com.accedo.wynkstudio.delegate.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.accedo.wynkstudio.delegate.EntitlementDelegate;
import com.accedo.wynkstudio.service.EntitlementService;

@Component
public class EntitlementDelegateImpl implements EntitlementDelegate {
	
	@Autowired
	EntitlementService entitlementService;
	
	@Override
	public String checkEntitlementStatus(String contentId, String cpToken, String userId, String tokenJson)
	{
		return entitlementService.checkEntitlementStatus(contentId, cpToken, userId, tokenJson);
	}

	@Override
	public String addMediaToBundle(String uid, String productId, String contentId) {
		return entitlementService.addMediaToBundle(uid, productId, contentId);
	}

}
