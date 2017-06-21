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

import org.hibernate.annotations.Type;

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
@Table(name = "user_offer")
public class Offer implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(nullable = false)
	private String offerId;

	private long offerValidity;
	
	private String offerStatus;
	
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean offerShownFlag;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserProfile userProfile;

	public Offer() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOfferId() {
		return offerId;
	}

	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}

	public long getOfferValidity() {
		return offerValidity;
	}

	public void setOfferValidity(long offerValidity) {
		this.offerValidity = offerValidity;
	}

	public String getofferStatus() {
		return offerStatus;
	}

	public void setofferStatus(String offerStatus) {
		this.offerStatus = offerStatus;
	}

	public Boolean getOfferShownFlag() {
		return offerShownFlag;
	}

	public void setOfferShownFlag(boolean offerShownFlag) {
		this.offerShownFlag = offerShownFlag;
	}

	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

	
	
}