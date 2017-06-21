package com.accedo.wynkstudio.delegate.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.accedo.wynkstudio.delegate.UserDelegate;
import com.accedo.wynkstudio.service.UserService;
import com.eclipsesource.json.JsonObject;

@Component
public class UserDelegateImpl implements UserDelegate {

	@Autowired
	UserService userService;

	@Override
	public String getUserById(String userId, String contextPath, String token, String deviceId) {
		return userService.getUserById(userId, contextPath, token, deviceId);
	}

	@Override
	public String setUserById(String userId, String userInfoJson) {
		return userService.setUserById(userId, userInfoJson);
	}

	@Override
	public String createUserById(String userId, String userInfoJson) {
		return userService.createUserById(userId, userInfoJson);
	}

	@Override
	public String updateUserById(String userId, String dataKey, String userInfoJson) {
		return userService.updateUserById(userId, dataKey, userInfoJson);
	}

	@Override
	public String getFavourites(String userId, String dataKey, String cpIds) {
		return userService.getFavourites(userId, dataKey, cpIds);
	}

	@Override
	public String getRecentList(String userId, String dataKey, String cpIds) {
		return userService.getRecentList(userId, dataKey, cpIds);
	}

	@Override
	public String removeAppgridDataBykey(String userId, String dataKey, String userInfoJson) {
		return userService.removeAppgridDataBykey(userId, dataKey, userInfoJson);
	}

	@Override
	public String getMsp() {
		return userService.getMsp();
	}

	@Override
	public JsonObject getMetadata() {
		return userService.getMetadata();
	}
	@Override
	public String refreshMetadata() {
		return userService.refreshMetadata();
	}

	@Override
	public String getUserTokenFromMPX(String userId, String contextPath, String token, String bsbResponse) {
		return userService.getUserTokenFromMPX(userId, contextPath, token, bsbResponse);
	}

	@Override
	public String updateUserRecentList(String userId, String userInfoJson) {
		return userService.updateUserRecentList(userId, userInfoJson);
	}

	@Override
	public String updateUserFavouriteList(String userId, String userInfoJson) {
		return userService.updateUserFavouriteList(userId, userInfoJson);
	}

	@Override
	public String getRails(String userId, String token, Boolean airtel, String bsbResponse, String deviceId, boolean showOffer) {
		return userService.getRails(userId, token, airtel, bsbResponse, deviceId, showOffer);
	}

	@Override
	public String getGiftInfo(String userId) {
		return userService.getGiftProductsInfo(userId);
	}

	@Override
	public String removeRecent(String userId, String assetId) {
		return userService.removeRecent(userId, assetId);
	}

	@Override
	public String getCards(String userId, String token, Boolean airtel, String bsbResponse, String deviceId,
			boolean showOffer) {
		return userService.getCards(userId, token, airtel, bsbResponse, deviceId, showOffer);
	}
}
