package com.accedo.wynkstudio.service;

import org.json.simple.parser.ParseException;

import com.accedo.wynkstudio.vo.UserProfileVO;

public interface CpLinkingService {

	public String getUrlFromEros(String contentId, String uid, int count) throws ParseException;

	public String getNewProfiles(String cpToken, String contentId, String token, String uid);
	
	public String purchase(String uid, String plan, UserProfileVO userProfileVO);
	
	public String cancelPurchase(String uid, String plan, UserProfileVO userProfileVO);
}
