package com.accedo.wynkstudio.mongodb.entity;

import java.io.Serializable;

/**
 * @author Accedo Software Private Limited
 * @version 1.0
 * @since 2014-07-01
 *        <p>
 *        The persistent class for the user_recent database table.
 */
/**
 * @author Suresh
 *
 */
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	
	private String msisdnDetected;

	private String status;

	private String uid;
	
	private String token;
	
	private String operator;

	private String circle;
	
	private String userType;
	
	private String icrCircle;
	
	private String eapSim;

	public User() {
	}

	public String getMsisdnDetected() {
		return msisdnDetected;
	}

	public void setMsisdnDetected(String msisdnDetected) {
		this.msisdnDetected = msisdnDetected;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getCircle() {
		return circle;
	}

	public void setCircle(String circle) {
		this.circle = circle;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getIcrCircle() {
		return icrCircle;
	}

	public void setIcrCircle(String icrCircle) {
		this.icrCircle = icrCircle;
	}

	public String getEapSim() {
		return eapSim;
	}

	public void setEapSim(String eapSim) {
		this.eapSim = eapSim;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}