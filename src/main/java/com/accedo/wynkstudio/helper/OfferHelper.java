package com.accedo.wynkstudio.helper;

import java.util.ArrayList;
import java.util.List;

import com.accedo.wynkstudio.entity.Offer;
import com.accedo.wynkstudio.entity.UserProfile;
import com.accedo.wynkstudio.vo.OfferVO;

public class OfferHelper {
	
	
	/* Get AppGrid Session Key */
	public static OfferVO toOfferVO(Offer offer) {
		OfferVO offerVO = new OfferVO();
		offerVO.setOfferId(offer.getOfferId());
		offerVO.setOfferStatus(offer.getofferStatus());
		offerVO.setOfferValidity(offer.getOfferValidity());
		offerVO.setOfferShownFlag(true);
		return offerVO;
	}
	
	public static Offer toOffer(OfferVO offerVO) {
		Offer offer = new Offer();
		if(offerVO.getOfferId() != null){
			offer.setOfferId(offerVO.getOfferId());
			offer.setOfferShownFlag(offerVO.isOfferShownFlag());
			offer.setofferStatus(offerVO.getOfferStatus());
			offer.setOfferValidity(offer.getOfferValidity());
		}
		return offer;
	}
	
	public static List<Offer> toFavouriteList(List<OfferVO> offerVOs, UserProfile userProfile) {
		List<Offer> offers = new ArrayList<Offer>();
		for (OfferVO offerVO : offerVOs) {
			Offer offer = toOffer(offerVO);
			offer.setUserProfile(userProfile);
			offers.add(offer);
		}
		
		return offers;
	}
	
	
}