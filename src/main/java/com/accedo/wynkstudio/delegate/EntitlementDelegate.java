package com.accedo.wynkstudio.delegate;

public interface EntitlementDelegate {
	
	public String checkEntitlementStatus(String contentId, String cpToken, String userId, String tokenJson);

	public String addMediaToBundle(String uid, String productId, String contentId);

}
