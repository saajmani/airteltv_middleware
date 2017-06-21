package com.accedo.wynkstudio.dao;

import com.accedo.wynkstudio.entity.UserProfile;
import com.accedo.wynkstudio.vo.UserProfileVO;

public interface UserProfileDao {

	public String createUserProfile(UserProfile userProfile);
	
	public String updateUserProfile(UserProfile userProfile);
	
	public UserProfileVO getUserProfileByUserId(String userId);

	boolean updateUserProfileCreatedflag(String userId, boolean createdFlag);

	public UserProfileVO getUserLoginDetails(String userId);
	
	public boolean getHooqTrialFlag(String userId);
	
	public boolean setHooqTrialFlag(String userId);
	
	public String updateUserLoginDetailse(String userId, String erosnowUserName, String erosnowUserToken, String erosnowUserTokenSecret);

	public boolean getHooqTrialFlagIos(String userId);

	public boolean setHooqTrialFlagIos(String userId);
}
