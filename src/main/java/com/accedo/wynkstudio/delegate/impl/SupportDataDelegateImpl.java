package com.accedo.wynkstudio.delegate.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.accedo.wynkstudio.delegate.SupportDataDelegate;
import com.accedo.wynkstudio.service.SupportDataService;

@Component
public class SupportDataDelegateImpl implements SupportDataDelegate {
	
	@Autowired
	SupportDataService supportDataService;
	
	public String getUserById(String userId)
	{
		return supportDataService.getUserById(userId);
	}

	public String getUserBasicInfo(String userId)
	{
		return supportDataService.getUserBasicInfo(userId);
	}

	public String getUserFavourites(String userId)
	{
		return supportDataService.getUserFavourites(userId);
	}

	public String getUserRecent(String userId)
	{
		return supportDataService.getUserRecent(userId);
	}

	public String getUserPacks(String userId)
	{
		return supportDataService.getUserPacks(userId);
	}

	public String getUserBundles(String userId)
	{
		return supportDataService.getUserBundles(userId);
	}

}
