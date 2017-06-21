package com.accedo.wynkstudio.helper;

import com.accedo.wynkstudio.entity.PersonalizedRail;
import com.accedo.wynkstudio.entity.UserProfile;
import com.accedo.wynkstudio.vo.PersonalizedRailVO;
import com.accedo.wynkstudio.vo.UserProfileVO;

public class PersonalizedRailHelper {

	public static PersonalizedRailVO toPersonalizedRailVO(PersonalizedRail personalizedRail) {
		PersonalizedRailVO personalizedRailVO = new PersonalizedRailVO();
		personalizedRailVO.setId(String.valueOf(personalizedRail.getId()));
		personalizedRailVO.setRailType(personalizedRail.getRailType());
		personalizedRailVO.setRailIds(personalizedRail.getRailIds());
		if (personalizedRail.getUserProfile() != null) {
			UserProfileVO userProfileVO = new UserProfileVO();
			userProfileVO.setUserId(personalizedRail.getUserProfile().getUserId());
			personalizedRailVO.setUserProfileVO(userProfileVO);
		}
		return personalizedRailVO;
	}

	public static PersonalizedRail toPersonalizedRail(PersonalizedRailVO personalizedRailVO) {
		PersonalizedRail personalizedRail = new PersonalizedRail();
		if(personalizedRailVO.getId() != null && !personalizedRailVO.getId().isEmpty()){
			personalizedRail.setId(Long.parseLong(personalizedRailVO.getId()));
		}
		personalizedRail.setRailType(personalizedRailVO.getRailType());
		personalizedRail.setRailIds(personalizedRailVO.getRailIds());
		if (personalizedRailVO.getUserProfileVO() != null) {
			UserProfile userProfile = new UserProfile();
			userProfile.setUserId(personalizedRailVO.getUserProfileVO().getUserId());
			personalizedRail.setUserProfile(userProfile);
		}
		return personalizedRail;
	}

}
