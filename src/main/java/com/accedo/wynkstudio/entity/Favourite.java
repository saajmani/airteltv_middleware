package com.accedo.wynkstudio.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
@Table(name = "user_favourite")
public class Favourite implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(nullable = false)
	private String assetId;

	private String cpToken;
	
	private String lastWatchedPosition;
	
	private String lastWatchedTime;

	private String duration;
	
	private String downloadedDate;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserProfile userProfile;

	public Favourite() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public String getCpToken() {
		return cpToken;
	}

	public void setCpToken(String cpToken) {
		this.cpToken = cpToken;
	}

	public String getLastWatchedPosition() {
		return lastWatchedPosition;
	}

	public void setLastWatchedPosition(String lastWatchedPosition) {
		this.lastWatchedPosition = lastWatchedPosition;
	}

	public String getLastWatchedTime() {
		return lastWatchedTime;
	}

	public void setLastWatchedTime(String lastWatchedTime) {
		this.lastWatchedTime = lastWatchedTime;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getDownloadedDate() {
		return downloadedDate;
	}

	public void setDownloadedDate(String downloadedDate) {
		this.downloadedDate = downloadedDate;
	}

	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

	
	
}