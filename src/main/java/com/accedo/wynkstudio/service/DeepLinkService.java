package com.accedo.wynkstudio.service;

public interface DeepLinkService {

	public String create(String url);

	public String getAll();

	public String getUrlByHash(String hashCode);

	public String getOfferImage(String offerId);

}
