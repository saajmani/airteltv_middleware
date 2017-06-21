package com.accedo.wynkstudio.delegate.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.accedo.wynkstudio.delegate.DiscoverDelegate;
import com.accedo.wynkstudio.service.DiscoverService;

@Component
public class DiscoverDelegateImpl implements DiscoverDelegate {

	@Autowired
	DiscoverService discoverService;

	@Override
	public String getFilters() {
		return discoverService.getFilters();
	}

	@Override
	public String getFilterContent(String uid, String filter,String languages, String programType, String sortingKey, String totalPageSize, String pageSize, String pageNumber) {
		return discoverService.getFilterContent(uid, filter,languages, programType, sortingKey, totalPageSize, pageSize, pageNumber);
	}
	public String getFiltersByUid(String uId, String count, String home) {

		return discoverService.getFiltersByUid(uId, count, home);
	}

	@Override
	public String deleteFilterByUid(String uId, String filterName) {

		return discoverService.deleteFilterByUid(uId, filterName);
	}

	@Override
	public String updateFilterById(String uId, String userInfoJson) {

		return discoverService.updateFilterByUid(uId, userInfoJson);

	}

	@Override
	public String createFilterById(String uId, String userInfoJson) {
		String response = "";
		response = discoverService.createFilterByUid(uId, userInfoJson);
		return response;
	}

}
