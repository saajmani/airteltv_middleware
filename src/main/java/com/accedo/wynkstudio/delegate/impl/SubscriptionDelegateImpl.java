package com.accedo.wynkstudio.delegate.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.accedo.wynkstudio.delegate.SubscriptionDelegate;
import com.accedo.wynkstudio.service.SubscriptionService;

@Component
public class SubscriptionDelegateImpl implements SubscriptionDelegate {
	
	@Autowired
	SubscriptionService subscriptionService;
	
	@Override
	public String setEntitlementsForUser(String uid, String product, String contextPath, String token, String cpId) {
		return subscriptionService.setEntitlementsForUser(uid, product, contextPath, token, cpId);
	}

	@Override
	public String unsubscribeUser(String uid, String product, String contextPath, String token, String cpId) {
		return subscriptionService.unsubscribeUser(uid, product, contextPath, token, cpId);
	}
	
	@Override
	public String getPackStatus(String uid, String token, String cpIds) {
		return subscriptionService.getPackStatus(uid, token, cpIds);
	}
	
	@Override
	public String setEntitlementsByBsb(String uid, String product, String contextPath, String token, String cpId)
	{
		return subscriptionService.setEntitlementsByBsb(uid, product, contextPath, token, cpId);
	}

	@Override
	public String activateProduct(String uid, String tokenString, String productId, String cpId, String deviceId, String platform, String deviceOs, String appVersion) {
		return subscriptionService.activateProduct(uid, tokenString, productId, cpId, deviceId, platform, deviceOs, appVersion);
	}

}
