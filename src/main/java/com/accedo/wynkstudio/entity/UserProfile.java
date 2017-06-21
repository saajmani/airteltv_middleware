package com.accedo.wynkstudio.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

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
@Table(name = "user_profile")
public class UserProfile implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "userId")
	private String userId;

	@Column(nullable = false)
	private String name;

	private String gender;
	
	private String email;
	
	private String dateOfBirth;
	
	private String mpxToken;
	
	private String cpToken;
	
	private String[] registeredChannels;
	
	private String erosnowUserName;
	
	private String erosnowUserToken;
	
	private String erosnowUserTokenSecret;
	
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean hooqTrialFlag;
	
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean hooqTrialFlagIos;
	
	@Column(name = "created")
	private Timestamp created;

	@PrePersist
	protected void onCreate() {
		created = new Timestamp(new java.util.Date().getTime());
	}

	
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean isCreatedFlag;
	
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean isSubscribedFlag;
	
	
	public boolean isSubscribedFlag() {
		return isSubscribedFlag;
	}
	
	public boolean getHooqTrialFlag() {
		return hooqTrialFlag;
	}
	
	public boolean getHooqTrialFlagIos() {
		return hooqTrialFlagIos;
	}

	public List<BundleCounter> getBundleCounters() {
		return bundleCounters;
	}


	public void setBundleCounters(List<BundleCounter> bundleCounters) {
		this.bundleCounters = bundleCounters;
	}


	public void setSubscribedFlag(boolean isSubscribedFlag) {
		this.isSubscribedFlag = isSubscribedFlag;
	}
	
	public void setHooqTrialFlag(boolean isHooqTrialFlag) {
		this.hooqTrialFlag = isHooqTrialFlag;
	}
	
	public void setHooqTrialFlagIos(boolean isHooqTrialFlagIos) {
		this.hooqTrialFlagIos = isHooqTrialFlagIos;
	}


	@OneToMany(mappedBy="userProfile",cascade=CascadeType.PERSIST,fetch = FetchType.LAZY)
	private List<Recent> recents;
	

	@OneToMany(mappedBy="userProfile",cascade=CascadeType.PERSIST,fetch = FetchType.LAZY)
	private List<Favourite> favourites;


	@OneToMany(mappedBy="userProfile", cascade=CascadeType.PERSIST,fetch = FetchType.LAZY)
	private List<Product> product;
	
	
	@OneToMany(mappedBy="userProfile", cascade=CascadeType.PERSIST,fetch = FetchType.LAZY)
	private List<BundleCounter> bundleCounters;
	
	public UserProfile() {
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}



	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}


	public String getDateOfBirth() {
		return dateOfBirth;
	}


	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}


	public boolean isCreatedFlag() {
		return isCreatedFlag;
	}


	public void setCreatedFlag(boolean isCreatedFlag) {
		this.isCreatedFlag = isCreatedFlag;
	}


	public String getMpxToken() {
		return mpxToken;
	}


	public void setMpxToken(String mpxToken) {
		this.mpxToken = mpxToken;
	}


	public String getCpToken() {
		return cpToken;
	}


	public void setCpToken(String cpToken) {
		this.cpToken = cpToken;
	}



	public List<Recent> getRecents() {
		return recents;
	}


	public void setRecents(List<Recent> recents) {
		this.recents = recents;
	}


	public List<Favourite> getFavourites() {
		return favourites;
	}


	public void setFavourites(List<Favourite> favourites) {
		this.favourites = favourites;
	}


	public List<Product> getProduct() {
		return product;
	}


	public void setProduct(List<Product> product) {
		this.product = product;
	}


	public String[] getRegisteredChannels() {
		return registeredChannels;
	}


	public void setRegisteredChannels(String[] registeredChannels) {
		this.registeredChannels = registeredChannels;
	}


	public String getErosnowUserName() {
		return erosnowUserName;
	}


	public void setErosnowUserName(String erosnowUserName) {
		this.erosnowUserName = erosnowUserName;
	}


	public String getErosnowUserToken() {
		return erosnowUserToken;
	}


	public void setErosnowUserToken(String erosnowUserToken) {
		this.erosnowUserToken = erosnowUserToken;
	}


	public String getErosnowUserTokenSecret() {
		return erosnowUserTokenSecret;
	}


	public void setErosnowUserTokenSecret(String erosnowUserTokenSecret) {
		this.erosnowUserTokenSecret = erosnowUserTokenSecret;
	}



	
	

}