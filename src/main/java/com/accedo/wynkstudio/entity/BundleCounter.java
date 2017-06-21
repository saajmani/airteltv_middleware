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
 *        The persistent class for the user_profile database table.
 */
/**
 * @author Suresh
 *
 */
@Entity
@Table(name = "user_bundlecounter")
public class BundleCounter implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(nullable = false)
	private String cpId;

	@Column(nullable = false)
	private String productId;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserProfile userProfile;
	
	private int itemLimit;
	
	private long counter;
	
	private String[] mediaList;
	
	public BundleCounter() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCpId() {
		return cpId;
	}

	public void setCpId(String cpId) {
		this.cpId = cpId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

	public int getItemLimit() {
		return itemLimit;
	}

	public void setItemLimit(int itemLimit) {
		this.itemLimit = itemLimit;
	}

	public long getCounter() {
		return counter;
	}

	public void setCounter(long counter) {
		this.counter = counter;
	}

	public String[] getMediaList() {
		return mediaList;
	}

	public void setMediaList(String[] mediaList) {
		this.mediaList = mediaList;
	}



}