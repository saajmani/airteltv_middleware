package com.accedo.wynkstudio.mongodb.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import com.accedo.wynkstudio.exception.BusinessApplicationException;
import com.accedo.wynkstudio.helper.UserDerivedProfileHelper;
import com.accedo.wynkstudio.mongodb.dao.MongoDBUserDerivedProfileDAO;
import com.accedo.wynkstudio.mongodb.entity.UserDerivedProfile;
import com.accedo.wynkstudio.util.WynkUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

@Repository
public class MongoDBUserDerivedProfileDAOImpl implements MongoDBUserDerivedProfileDAO {

	private DBCollection col;
	final Logger log = LoggerFactory.getLogger(this.getClass());

	@PostConstruct	
	public void init() {
		MongoClient mongo = null;
		try {
			mongo = new MongoClient(Arrays.asList(
					   new ServerAddress(WynkUtil.mongodbHostPrimary, Integer.parseInt(WynkUtil.mongodbPort)),
					   new ServerAddress(WynkUtil.mongodbHostSecondary, Integer.parseInt(WynkUtil.mongodbPort))));
			this.col = mongo.getDB(WynkUtil.mongodbName).getCollection("UserDerivedProfile");
		} catch (Exception e) {
			log.error("Mongo Error:", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
	}
	
	public UserDerivedProfile createUserDerivedProfile(UserDerivedProfile userDerivedProfile) {
		DBObject doc = UserDerivedProfileHelper.toDBObject(userDerivedProfile);
		this.col.insert(doc);
		ObjectId id = (ObjectId) doc.get("_id");
		userDerivedProfile.setId(id.toString());
		return userDerivedProfile;
	}

	public void updateUserDerivedProfile(UserDerivedProfile userDerivedProfile) {
		DBObject query = BasicDBObjectBuilder.start().append("_id", new ObjectId(userDerivedProfile.getId())).get();
		this.col.update(query, UserDerivedProfileHelper.toDBObject(userDerivedProfile));
	}

	public UserDerivedProfile readUserDerivedProfileById(String id) {
		DBObject query = BasicDBObjectBuilder.start().append("_id", new ObjectId(id)).get();
		DBObject data = this.col.findOne(query);
		return UserDerivedProfileHelper.toUser(data);
	}

	public UserDerivedProfile readUserDerivedProfileByUserId(String userId) {
		BasicDBObject andQuery = new BasicDBObject();
		List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
		obj.add(new BasicDBObject("uid", userId));
		andQuery.put("$and", obj);
		UserDerivedProfile userDerivedProfile = null;
		DBObject orderBy = new BasicDBObject();
		orderBy.put( "_id", -1 );;
		DBCursor cursor = col.find(andQuery).limit(1).sort(orderBy);
		while (cursor.hasNext()) {
			DBObject doc = cursor.next();
			userDerivedProfile = UserDerivedProfileHelper.toUser(doc);
		}
		return userDerivedProfile;
	}
	
	
	
}
