package com.accedo.wynkstudio.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.accedo.wynkstudio.dao.OfferDao;
import com.accedo.wynkstudio.entity.Offer;
import com.accedo.wynkstudio.entity.Product;
import com.accedo.wynkstudio.helper.OfferHelper;
import com.accedo.wynkstudio.helper.ProducttHelper;
import com.accedo.wynkstudio.vo.OfferVO;
import com.accedo.wynkstudio.vo.ProductVO;


/**
 * @author Accedo Software Private Limited
 * @version 1.0
 * @since 2014-07-01
 */
@Repository
public class OfferDaoImpl implements OfferDao  {

	private EntityManager entityManager;

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean createOffer(Offer offer) {
		boolean retVal = true;
		try {
			entityManager.persist(offer);
		} catch (Exception e) {
			return false;
		}
		return retVal;
	}


	@Override
	public List<OfferVO> getOfferListByUserId(String userId) {
		List<OfferVO> OfferVOs = new ArrayList<OfferVO>();
		Query q = entityManager.createQuery(
				"SELECT of FROM Offer of where of.userProfile.userId = '" + userId + "'");  
		List<Offer> Offers = q.getResultList();
		if(Offers != null && Offers.size() > 0){
			for (Offer offer : Offers) {
				OfferVOs
				.add(OfferHelper.toOfferVO(offer));
			}
		}
		
		return OfferVOs;
	}

	@Override
	public boolean createOfferList(List<Offer> offers) {
		boolean retVal = true;
		try {
			entityManager.persist(offers);
		} catch (Exception e) {
		}
		return retVal;
	}

	@Override
	public OfferVO getOfferByUserIdOfferId(String userId, String offerId) {
		OfferVO offerVO = null;
		try {
			String query = "SELECT pt FROM Offer pt where pt.offerId = '"  + offerId  + "' and pt.userProfile.userId = '" + userId + "'";  
			Query q = entityManager.createQuery(query);
			Offer offer = (Offer) q.getResultList().get(0);
			if(offer != null){
				offerVO = OfferHelper.toOfferVO(offer);
			}
		} catch (Exception e) {
			return offerVO;
		}

		return offerVO;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean updateOfferValidity(String offerId, String uid, long validity) {
		try {
			Query q = entityManager.createQuery("update from Offer pt set offerValidity= " + validity + " where pt.userProfile.userId = '" + uid + "' and offerId='" + offerId + "'");
			q.executeUpdate();
			entityManager.flush();
	} catch (Exception e) {
		return false;
	}
		return true;
	}
}
