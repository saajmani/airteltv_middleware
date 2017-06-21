package com.accedo.wynkstudio.delegate;

public interface SubscriptionDelegate {
	
	public String setEntitlementsForUser(String uid, String product, String contextPath, String token, String cpId);

	public String unsubscribeUser(String uid, String product, String contextPath, String token, String cpId);

	public String getPackStatus(String uid, String token, String cpIds);

	public String setEntitlementsByBsb(String uid, String product, String contextPath, String token, String cpId);

	public String activateProduct(String uid, String productId, String platform);

}
