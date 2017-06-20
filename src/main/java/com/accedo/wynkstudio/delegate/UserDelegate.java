package com.accedo.wynkstudio.delegate;

import com.eclipsesource.json.JsonObject;

public interface UserDelegate {

	public String getUserById(String userId, String contextPath, String token, String deviceId);
        
        public String getUserByIdWithVersion(String userId, String contextPath, String token, String deviceId, String platform, String appVersion);

	public String setUserById(String userId, String userInfoJson);

	public String createUserById(String userId, String userInfoJson);

	public String updateUserById(String userId, String dataKey, String userInfoJson);

	public String getFavourites(String userId, String dataKey, String cpIds);

	public String getRecentList(String userId, String dataKey, String cpIds);

	public String removeAppgridDataBykey(String userId, String dataKey, String userInfoJson);

	public String getMsp();

	public JsonObject getMetadata();
	
	public String refreshMetadata();

	public String getUserTokenFromMPX(String userId, String contextPath, String token, String bsbResponse);
	
	public String updateUserRecentList(String userId, String userInfoJson);
	
	public String updateUserFavouriteList(String userId, String userInfoJson);

	public String getRails(String userId, String token, Boolean airtel, String bsbResponse, String deviceId, boolean showOffer);

	public String getGiftInfo(String userId, String token, String deviceId, String deviceOs, String appVersion);
	
	public String removeRecent(String userId, String assetId);
	
	public String getCards(String userId, String token, Boolean airtel, String bsbResponse, String deviceId, boolean showOffer, String deviceOs, String appVersion);

}
