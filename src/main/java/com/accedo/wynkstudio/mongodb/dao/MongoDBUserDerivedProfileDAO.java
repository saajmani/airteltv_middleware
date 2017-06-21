package com.accedo.wynkstudio.mongodb.dao;

import java.util.List;

import com.accedo.wynkstudio.mongodb.entity.UserDerivedProfile;

public interface MongoDBUserDerivedProfileDAO {


	public UserDerivedProfile createUserDerivedProfile(UserDerivedProfile userDerivedProfile);
	
	public void updateUserDerivedProfile(UserDerivedProfile userDerivedProfile);
	
	public UserDerivedProfile readUserDerivedProfileById(String id);
	
	public UserDerivedProfile readUserDerivedProfileByUserId(String userId);
	
	
}

