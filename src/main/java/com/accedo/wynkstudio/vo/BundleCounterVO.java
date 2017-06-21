package com.accedo.wynkstudio.vo;



public class BundleCounterVO {

	
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

	public UserProfileVO getUserProfileVO() {
		return userProfileVO;
	}
	public void setUserProfileVO(UserProfileVO userProfileVO) {
		this.userProfileVO = userProfileVO;
	}


	private String id;
	private String cpId;
	private String productId;
	private int itemLimit ;
	private long counter;
	private String[] mediaList;
	private UserProfileVO userProfileVO;
}
