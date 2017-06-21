package com.accedo.wynkstudio.helper;

import com.accedo.wynkstudio.entity.BundleCounter;
import com.accedo.wynkstudio.entity.UserProfile;
import com.accedo.wynkstudio.vo.BundleCounterVO;
import com.accedo.wynkstudio.vo.UserProfileVO;

public class BundleCounterHelper {
	
	
	/* Get AppGrid Session Key */
	public static BundleCounterVO toBundleCounterVO(BundleCounter bundleCount) {
		BundleCounterVO bundleCounterVO = new BundleCounterVO();
		bundleCounterVO.setProductId(bundleCount.getProductId());
		bundleCounterVO.setCpId(bundleCount.getCpId());
		bundleCounterVO.setId(String.valueOf(bundleCount.getId()));
		bundleCounterVO.setItemLimit(bundleCount.getItemLimit());
		bundleCounterVO.setCounter(bundleCount.getCounter());
		bundleCounterVO.setMediaList(bundleCount.getMediaList());
		if (bundleCount.getUserProfile() != null) {
			UserProfileVO userProfileVO = new UserProfileVO();
			userProfileVO.setUserId(bundleCount.getUserProfile().getUserId());
			bundleCounterVO.setUserProfileVO(userProfileVO);
		}
		return bundleCounterVO;
	}

	public static BundleCounter toBundleCounter(BundleCounterVO bundleCounterVO) {
		BundleCounter bundleCounter = new BundleCounter();
		bundleCounter.setProductId(bundleCounterVO.getProductId());
		bundleCounter.setCpId(bundleCounterVO.getCpId());
		bundleCounter.setId(Long.parseLong(bundleCounterVO.getId()));
		bundleCounter.setItemLimit(bundleCounterVO.getItemLimit());
		bundleCounter.setCounter(bundleCounterVO.getCounter());
		bundleCounter.setMediaList(bundleCounterVO.getMediaList());
		if (bundleCounterVO.getUserProfileVO() != null) {
			UserProfile userProfile = new UserProfile();
			userProfile.setUserId(bundleCounterVO.getUserProfileVO().getUserId());
			bundleCounter.setUserProfile(userProfile);
		}
		return bundleCounter;
	}
	
}