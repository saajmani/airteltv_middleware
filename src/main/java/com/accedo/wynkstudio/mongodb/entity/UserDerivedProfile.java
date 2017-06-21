package com.accedo.wynkstudio.mongodb.entity;

import java.io.Serializable;

public class UserDerivedProfile implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String userId;
	private String[] language;
	private String userType;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String[] getLanguage() {
		return language;
	}
	public void setLanguage(String[] language) {
		this.language = language;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	
}
