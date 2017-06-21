package com.accedo.wynkstudio.vo;

import java.util.List;


public class UserProfileVO {

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMpxToken() {
		return mpxToken;
	}
	public void setMpxToken(String mpxToken) {
		this.mpxToken = mpxToken;
	}
	
	public String getCpToken() {
		return cpToken;
	}
	public void setCpToken(String cpToken) {
		this.cpToken = cpToken;
	}
	public String getLoginUserName() {
		return loginUserName;
	}
	public void setLoginUserName(String loginUserName) {
		this.loginUserName = loginUserName;
	}
	public String getLoginUserPassword() {
		return loginUserPassword;
	}
	public void setLoginUserPassword(String loginUserPassword) {
		this.loginUserPassword = loginUserPassword;
	}

	public List<FavouriteVO> getFavoriteMovies() {
		return favoriteMovies;
	}
	public void setFavoriteMovies(List<FavouriteVO> favoriteMovies) {
		this.favoriteMovies = favoriteMovies;
	}
	public List<RecentVO> getLastWatchedMovies() {
		return lastWatchedMovies;
	}
	public void setLastWatchedMovies(List<RecentVO> lastWatchedMovies) {
		this.lastWatchedMovies = lastWatchedMovies;
	}
	public List<ProductVO> getSubscribedChannels() {
		return subscribedChannels;
	}
	public void setSubscribedChannels(List<ProductVO> subscribedChannels) {
		this.subscribedChannels = subscribedChannels;
	}


	public boolean getIsCreatedFlag() {
		return isCreatedFlag;
	}
	public void setIsCreatedFlag(boolean isCreatedFlag) {
		this.isCreatedFlag = isCreatedFlag;
	}
	public boolean getIsSubscribedFlag() {
		return isSubscribedFlag;
	}
	public void setIsSubscribedFlag(boolean isSubscribedFlag) {
		this.isSubscribedFlag = isSubscribedFlag;
	}
	public boolean getHooqTrialFlag() {
		return hooqTrialFlag;
	}
	public void setHooqTrialFlag(boolean hooqTrialFlag) {
		this.hooqTrialFlag = hooqTrialFlag;
	}
	public boolean getHooqTrialFlagIos() {
		return hooqTrialFlagIos;
	}
	public void setHooqTrialFlagIos(boolean hooqTrialFlagIos) {
		this.hooqTrialFlagIos = hooqTrialFlagIos;
	}


	public String[] getRegisteredChannels() {
		return registeredChannels;
	}
	public void setRegisteredChannels(String[] registeredChannels) {
		this.registeredChannels = registeredChannels;
	}


	public List<BundleCounterVO> getBundleCounter() {
		return bundleCounter;
	}
	public void setBundleCounter(List<BundleCounterVO> bundleCounter) {
		this.bundleCounter = bundleCounter;
	}


	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getErosnowUserName() {
		return erosnowUserName;
	}
	public void setErosnowUserName(String erosnowUserName) {
		this.erosnowUserName = erosnowUserName;
	}
	public String getErosnowUserToken() {
		return erosnowUserToken;
	}
	public void setErosnowUserToken(String erosnowUserToken) {
		this.erosnowUserToken = erosnowUserToken;
	}
	public String getErosnowUserTokenSecret() {
		return erosnowUserTokenSecret;
	}
	public void setErosnowUserTokenSecret(String erosnowUserTokenSecret) {
		this.erosnowUserTokenSecret = erosnowUserTokenSecret;
	}


	private String userId;
	private String name;
	private String gender;
	private String email;
	private String dob;
	private String mpxToken;
	private String cpToken;
	private String loginUserName;
	private String loginUserPassword;
	private boolean isCreatedFlag;
	private boolean isSubscribedFlag;
	private boolean hooqTrialFlag;
	private boolean hooqTrialFlagIos;
	private List<FavouriteVO> favoriteMovies;
	private List<RecentVO> lastWatchedMovies;
	private List<ProductVO> subscribedChannels;
	private List<BundleCounterVO> bundleCounter;
	private String[] registeredChannels;
	private String erosnowUserName;
	private String erosnowUserToken;
	private String erosnowUserTokenSecret;
		
}
