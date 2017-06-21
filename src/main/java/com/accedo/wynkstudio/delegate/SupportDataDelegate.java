package com.accedo.wynkstudio.delegate;

public interface SupportDataDelegate {
	
	public String getUserById(String userId);

	public String getUserBasicInfo(String userId);

	public String getUserFavourites(String userId);

	public String getUserRecent(String userId);

	public String getUserPacks(String userId);

	public String getUserBundles(String userId);

}
