package com.accedo.wynkstudio.vo;

public class PersonalizedRailVO {
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public UserProfileVO getUserProfileVO() {
		return userProfileVO;
	}
	public void setUserProfileVO(UserProfileVO userProfileVO) {
		this.userProfileVO = userProfileVO;
	}
	public String getRailType() {
		return railType;
	}
	public void setRailType(String railType) {
		this.railType = railType;
	}
	public String[] getRailIds() {
		return railIds;
	}
	public void setRailIds(String[] railIds) {
		this.railIds = railIds;
	}


	private String id;
	private String railType;
	private String[] railIds;
	private UserProfileVO userProfileVO;

}
