package com.accedo.wynkstudio.service;

public interface EntitlementService {
	
	public String checkEntitlementStatus(String contentId, String cpToken, String userId, String tokenJson);
	
	public String addMediaToBundle(String uid, String productId, String contentId);

}
