package com.accedo.wynkstudio.helper;

import org.bson.types.ObjectId;

import com.accedo.wynkstudio.mongodb.entity.User;
import com.eclipsesource.json.JsonObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

public class UserHelper {

	// convert User Object to MongoDB DBObject
	// take special note of converting id String to ObjectId
	public static DBObject toDBObject(User User) {

		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start()
				.append("msisdnDetected",User.getMsisdnDetected()).append("status", User.getStatus())
				.append("uid",User.getUid()).append("token", User.getToken())
		.append("operator",User.getOperator())
		.append("circle",User.getCircle()).append("userType", User.getUserType())
		.append("icrCircle", User.getIcrCircle())
		.append("eapSim", User.getEapSim());
		if (User.getId() != null){
			builder = builder.append("_id", new ObjectId(User.getId()));
		}
		return builder.get();
	}

	// convert DBObject Object to User
	// take special note of converting ObjectId to String
	public static User toUser(DBObject doc) {
		User user = new User();
		user.setMsisdnDetected((String) doc.get("msisdnDetected"));
		user.setStatus((String) doc.get("status"));
		user.setUid((String) doc.get("uid"));
		user.setToken((String)doc.get("token"));
		user.setOperator((String) doc.get("operator"));
		user.setCircle((String) doc.get("circle"));
		user.setUserType((String) doc.get("userType"));
		user.setIcrCircle((String) doc.get("icrCircle"));
		user.setEapSim((String) doc.get("eapSim"));
		ObjectId id = (ObjectId) doc.get("_id");
		user.setId(id.toString());
		return user;

	}
	
	/* Extract User Object from BSB User Profile */
	public static User extractUserFromJson(String userJson, String uid) {
		User user = new User();
		JsonObject profileJsonObject = JsonObject.readFrom(userJson);
		user.setMsisdnDetected("");
		user.setStatus("");
		user.setUid(uid);
		user.setToken("");
		user.setOperator((profileJsonObject.get("operator") != null && !profileJsonObject.get("operator").isNull()) ? profileJsonObject
				.get("operator").asString() : "");
		user.setCircle((profileJsonObject.get("circle") != null && !profileJsonObject.get("circle").isNull()) ? profileJsonObject
				.get("circle").asString() : "");
		user.setUserType((profileJsonObject.get("userType") != null && !profileJsonObject.get("userType").isNull()) ? profileJsonObject
				.get("userType").asString() : "");
		user.setIcrCircle(profileJsonObject.get("icrCircle").asBoolean() ? "true" : "false");
		user.setEapSim("");
		return user;
	}
	
}
