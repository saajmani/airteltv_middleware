package com.accedo.wynkstudio.vo;



public class ProductVO {

	
	public String getId() {
		return id;
	}
	public void setId(String id) {
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



	public long getBsbValidity() {
		return bsbValidity;
	}
	public void setBsbValidity(long bsbValidity) {
		this.bsbValidity = bsbValidity;
	}
	
	public boolean isMessageFlag() {
		return messageFlag;
	}
	public void setMessageFlag(boolean messageFlag) {
		this.messageFlag = messageFlag;
	}


	public long getMessageTimeStamp() {
		return messageTimeStamp;
	}
	public void setMessageTimeStamp(long messageTimeStamp) {
		this.messageTimeStamp = messageTimeStamp;
	}


	private String id;
	private String cpId;
	private String productId;
	private String state;
	private String productType;
	private boolean allowPlayback;
	private boolean renewal;
	private boolean active;
	private boolean live;
	private boolean bundleFlag;
	private boolean subscribeButtonState;
	private boolean unsubscribeButtonState;
	private long contentValidity;
	private long bsbValidity;
	private long messageTimeStamp;
	private boolean messageFlag;
	

}
