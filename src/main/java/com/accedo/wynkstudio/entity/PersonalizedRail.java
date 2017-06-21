package com.accedo.wynkstudio.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
@Entity
@Table(name = "user_personalized_rail", uniqueConstraints = @UniqueConstraint(columnNames = {
		"railType", "user_id" }))
public class PersonalizedRail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserProfile userProfile;

	private String railType;
	
	private String[] railIds;
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

 
	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
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

	
	
	
}