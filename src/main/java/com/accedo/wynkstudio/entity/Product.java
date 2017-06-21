package com.accedo.wynkstudio.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "user_product")
public class Product implements Serializable {
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

	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean allowPlayback;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean renewal;

	private String state;

	private String productType;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean subscribeButtonState;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean unsubscribeButtonState;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean bundleFlag;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean live;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean active;

	private long contentValidity;

	private long bsbValidity;
	
	private long messageTimeStamp;

	@SuppressWarnings("unused")
	private Timestamp created;

	@SuppressWarnings("unused")
	private Timestamp activated;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean messageFlag;

	public boolean isMessageFlag() {
		return messageFlag;
	}

	public void setMessageFlag(boolean messageFlag) {
		this.messageFlag = messageFlag;
	}

	@PrePersist
	protected void onCreate() {
		created = new Timestamp(new java.util.Date().getTime());
	}

	public Product() {
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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

	public boolean isAllowPlayback() {
		return allowPlayback;
	}

	public void setAllowPlayback(boolean allowPlayback) {
		this.allowPlayback = allowPlayback;
	}

	public boolean isRenewal() {
		return renewal;
	}

	public void setRenewal(boolean renewal) {
		this.renewal = renewal;
	}

	public boolean getBundleFlag() {
		return bundleFlag;
	}

	public void setBundleFlag(boolean bundleFlag) {
		this.bundleFlag = bundleFlag;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean getLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public boolean isSubscribeButtonState() {
		return subscribeButtonState;
	}

	public void setSubscribeButtonState(boolean subscribeButtonState) {
		this.subscribeButtonState = subscribeButtonState;
	}

	public boolean isUnsubscribeButtonState() {
		return unsubscribeButtonState;
	}

	public void setUnsubscribeButtonState(boolean unsubscribeButtonState) {
		this.unsubscribeButtonState = unsubscribeButtonState;
	}

	public long getContentValidity() {
		return contentValidity;
	}

	public void setContentValidity(long contentValidity) {
		this.contentValidity = contentValidity;
	}

	public long getBsbValidity() {
		return bsbValidity;
	}

	public void setBsbValidity(long bsbValidity) {
		this.bsbValidity = bsbValidity;
	}

	public long getMessageTimeStamp() {
		return messageTimeStamp;
	}

	public void setMessageTimeStamp(long messageTimeStamp) {
		this.messageTimeStamp = messageTimeStamp;
	}

}