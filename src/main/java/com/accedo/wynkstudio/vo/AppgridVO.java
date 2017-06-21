package com.accedo.wynkstudio.vo;

public class AppgridVO {

	
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
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assetId == null) ? 0 : assetId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AppgridVO other = (AppgridVO) obj;
		if (assetId == null) {
			if (other.assetId != null)
				return false;
		} else if (!assetId.equals(other.assetId))
			return false;
		return true;
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


	private String assetId;
	private String cpToken;
	private String lastWatchedPosition;
	private String lastWatchedTime;
	private String duration;
	private String downloadedDate;
	
}
