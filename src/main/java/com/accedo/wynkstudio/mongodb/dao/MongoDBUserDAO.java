package com.accedo.wynkstudio.mongodb.dao;

import com.accedo.wynkstudio.mongodb.entity.User;

public interface MongoDBUserDAO {

	public User createUser(User user);
	
	public void updateUser(User user);
	
	public User readUserById(String id);
	
	public User readUserByUserId(String userId);
	
	public int readUserCountByUserId(String userId);
	
	public void deleteFirstUser(String userId);
}
