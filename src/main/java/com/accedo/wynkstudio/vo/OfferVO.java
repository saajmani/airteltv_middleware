package com.accedo.wynkstudio.vo;

public class OfferVO {

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOfferId() {
		return offerId;
	}

	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}

	public String getOfferStatus() {
		return offerStatus;
	}

	public void setOfferStatus(String offerStatus) {
		this.offerStatus = offerStatus;
	}

	public boolean isOfferShownFlag() {
		return offerShownFlag;
	}

	public void setOfferShownFlag(boolean offerShownFlag) {
		this.offerShownFlag = offerShownFlag;
	}

	public long getOfferValidity() {
		return offerValidity;
	}

	public void setOfferValidity(long offerValidity) {
		this.offerValidity = offerValidity;
	}

	private String id;
	private String offerId;
	private String offerStatus;
	private boolean offerShownFlag;
	private long offerValidity;

}
