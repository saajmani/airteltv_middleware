package com.accedo.wynkstudio.service;

import com.eclipsesource.json.JsonObject;

public interface UserService {

	public String getUserById(String userId, String contextPath, String token, String deviceId);

	public String setUserById(String userId, String userInfoJson);

	public String updateUserById(String userId, String dataKey, String userInfoJson);

	public String getFavourites(String userId, String dataKey, String cpIds);

	public String getRecentList(String userId, String dataKey, String cpIds);

	public String removeAppgridDataBykey(String userId, String dataKey, String userInfoJson);
	
	public String getMsp();

	public JsonObject getMetadata();
	
	public String refreshMetadata();
	
	public String createUserById(String userId, String userInfoJson);

	public String getUserTokenFromMPX(String userId, String contextPath, String token, String bsbResponse);
	
	public String updateUserRecentList(String userId, String userInfoJson);
	
	public String updateUserFavouriteList(String userId, String userInfoJson);

	public String getRails(String userId, String token, Boolean airtel, String bsbResponse, String deviceId, boolean showOffer);

	public String getGiftProductsInfo(String userId);
	
	public String removeRecent(String userId, String assetId);
	
	public String getCards(String userId, String token, Boolean airtel, String bsbResponse, String deviceId, boolean showOffer);
	
}
