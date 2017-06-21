package com.accedo.wynkstudio.dao;

import java.util.List;

import com.accedo.wynkstudio.entity.Offer;
import com.accedo.wynkstudio.vo.OfferVO;

public interface OfferDao {

	public boolean createOffer(Offer offer);
	
	public List<OfferVO> getOfferListByUserId(String userId);
	
	public boolean createOfferList(List<Offer> offers);
	
	public boolean updateOfferValidity(String offerId, String uid, long validity);
	
	public OfferVO getOfferByUserIdOfferId(String userId, String offerId);
	
}
