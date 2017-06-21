package com.accedo.wynkstudio.helper;

import java.util.ArrayList;
import java.util.List;

import com.accedo.wynkstudio.entity.UserFilter;
import com.accedo.wynkstudio.entity.UserProfile;
import com.accedo.wynkstudio.vo.UserFilterVO;

public class UserFilterHelper {

	/* Get AppGrid Session Key */
	public static UserFilterVO toUserFilterVO(UserFilter userFilter) {

		UserFilterVO userFilterVO = new UserFilterVO();
		userFilterVO.setuId(userFilter.getUserProfile().getUserId());
		userFilterVO.setFilter(userFilter.getFilter());
		userFilterVO.setFilterType(userFilter.getFilterType());
		userFilterVO.setFilterName(userFilter.getFilterName());
		userFilterVO.setSortingKey(userFilter.getSortingKey());
		userFilterVO.setSortingKey(userFilter.getSortingKey());
		userFilterVO.setLanguages(userFilter.getLanguages());
		
		return userFilterVO;
	}

	public static UserFilter toUserFilter(UserFilterVO userFilterVO) {
		UserFilter userFilter = new UserFilter();
		UserProfile userProfile = null;
		if (userFilterVO.getuId() != null) {
			userProfile = new UserProfile();
			userProfile.setUserId(userFilterVO.getuId());
			userFilter.setUserProfile(userProfile);
			userFilter.setFilter(userFilterVO.getFilter());
			userFilter.setFilterType(userFilterVO.getFilterType());
			userFilter.setFilterName(userFilterVO.getFilterName());
			userFilter.setLanguages(userFilterVO.getLanguages());
		}
		return userFilter;
	}

	public static List<UserFilter> toRecentList(List<UserFilterVO> userFilterVOs, UserProfile userProfile) {
		List<UserFilter> userFilters = new ArrayList<UserFilter>();
		for (UserFilterVO userFilterVO : userFilterVOs) {
			UserFilter userFilter = toUserFilter(userFilterVO);
			userFilters.add(userFilter);

		}
		return userFilters;
	}
}
