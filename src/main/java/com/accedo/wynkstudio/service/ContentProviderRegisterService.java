package com.accedo.wynkstudio.service;

public interface ContentProviderRegisterService {

	public Object getContentProvider(String tokenId);

	public void setContentProvider(String tokenId, Object object);

	public String getContentProviderList();

}
