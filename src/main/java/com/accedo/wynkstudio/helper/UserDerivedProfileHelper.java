package com.accedo.wynkstudio.helper;

import java.util.List;

import org.bson.types.ObjectId;

import com.accedo.wynkstudio.mongodb.entity.UserDerivedProfile;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

public class UserDerivedProfileHelper {

	public static DBObject toDBObject(UserDerivedProfile userDerivedProfile) {

		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start()
				.append("uid",userDerivedProfile.getUserId())
				.append("language",userDerivedProfile.getLanguage())
				.append("userType",userDerivedProfile.getUserType());
		if (userDerivedProfile.getId() != null){
			builder = builder.append("_id", new ObjectId(userDerivedProfile.getId()));
		}
		return builder.get();
	}
	
	public static UserDerivedProfile toUser(DBObject doc) {
		UserDerivedProfile userDerivedProfile = new UserDerivedProfile();
		userDerivedProfile.setUserId((String) doc.get("uid"));
		BasicDBList languageList = (BasicDBList) doc.get("language");
		userDerivedProfile.setLanguage(languageList.toArray(new String[languageList.size()]));
		if(doc.get("userType") != null){
			userDerivedProfile.setUserType((String) doc.get("userType"));
		}
		ObjectId id = (ObjectId) doc.get("_id");
		userDerivedProfile.setId(id.toString());
		return userDerivedProfile;

	}
	
	
	
}
