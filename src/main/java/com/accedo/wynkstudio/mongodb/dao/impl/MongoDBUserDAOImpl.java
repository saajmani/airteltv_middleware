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
import com.accedo.wynkstudio.helper.UserHelper;
import com.accedo.wynkstudio.mongodb.dao.MongoDBUserDAO;
import com.accedo.wynkstudio.mongodb.entity.User;
import com.accedo.wynkstudio.util.WynkUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

//DAO class for different MongoDB CRUD operations
//take special note of "id" String to ObjectId conversion and vice versa
//also take note of "_id" key for primary key
@Repository
public class MongoDBUserDAOImpl implements MongoDBUserDAO{

	private DBCollection col;
	final Logger log = LoggerFactory.getLogger(this.getClass());

	@PostConstruct
	public void init() {
		MongoClient mongo = null;
		try {
			mongo = new MongoClient(Arrays.asList(
					   new ServerAddress(WynkUtil.mongodbHostPrimary, Integer.parseInt(WynkUtil.mongodbPort)),
					   new ServerAddress(WynkUtil.mongodbHostSecondary, Integer.parseInt(WynkUtil.mongodbPort))));
			this.col = mongo.getDB(WynkUtil.mongodbName).getCollection("User");
		} catch (Exception e) {
			log.error("Mongo Error:", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some error occured!");
		}
	}

	public User createUser(User user) {
		DBObject doc = UserHelper.toDBObject(user);
		this.col.insert(doc);
		ObjectId id = (ObjectId) doc.get("_id");
		user.setId(id.toString());
		return user;
	}

	public void updateUser(User user) {
		DBObject query = BasicDBObjectBuilder.start().append("_id", new ObjectId(user.getId())).get();
		this.col.update(query, UserHelper.toDBObject(user));
	}

	public List<User> readAllUser() {
		List<User> data = new ArrayList<User>();
		DBCursor cursor = col.find();
		while (cursor.hasNext()) {
			DBObject doc = cursor.next();
			User user = UserHelper.toUser(doc);
			data.add(user);
		}
		return data;
	}

	public void deleteUser(User user) {
		DBObject query = BasicDBObjectBuilder.start().append("_id", new ObjectId(user.getId())).get();
		this.col.remove(query);
	}

	public User readUserById(String id) {
		DBObject query = BasicDBObjectBuilder.start().append("_id", new ObjectId(id)).get();
		DBObject data = this.col.findOne(query);
		return UserHelper.toUser(data);
	}

	public User readUserByUserId(String userId) {
		BasicDBObject andQuery = new BasicDBObject();
		List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
		obj.add(new BasicDBObject("uid", userId));
		andQuery.put("$and", obj);
		User user = null;
		DBObject orderBy = new BasicDBObject();
		orderBy.put( "_id", -1 );;
		DBCursor cursor = col.find(andQuery).limit(1).sort(orderBy);
		while (cursor.hasNext()) {
			DBObject doc = cursor.next();
			user = UserHelper.toUser(doc);
		}
		return user;
	}

	public int readUserCountByUserId(String userId) {
		int count = 0;
		BasicDBObject andQuery = new BasicDBObject();
		List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
		obj.add(new BasicDBObject("uid", userId));
		andQuery.put("$and", obj);
		count = col.find(andQuery).count();
		return count;
	}

	public void deleteFirstUser(String userId) {

		BasicDBObject andQuery = new BasicDBObject();
		List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
		obj.add(new BasicDBObject("uid", userId));
		andQuery.put("$and", obj);
		DBObject cursor = this.col.findOne(andQuery);
		ObjectId id = (ObjectId) cursor.get("_id");
		DBObject query = BasicDBObjectBuilder.start().append("_id", new ObjectId(id.toString())).get();
		this.col.remove(query);
	}

}
