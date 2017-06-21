package com.accedo.wynkstudio.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.accedo.wynkstudio.dao.FavouriteDao;
import com.accedo.wynkstudio.entity.Favourite;
import com.accedo.wynkstudio.helper.FavouritetHelper;
import com.accedo.wynkstudio.vo.FavouriteVO;

/**
 * @author Accedo Software Private Limited
 * @version 1.0
 * @since 2014-07-01
 */
@Repository
public class FavouriteDaoImpl implements FavouriteDao  {

	private EntityManager entityManager;

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean createFavourite(Favourite favourite) {
		boolean retVal = true;
		try {
			entityManager.persist(favourite);
		} catch (Exception e) {
		}
		return retVal;
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean createFavouriteList(List<Favourite> favourites) {
		boolean retVal = true;
		try {
			entityManager.persist(favourites);
		} catch (Exception e) {
		}
		return retVal;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Favourite getFavouriteByUserIdWithAssetId(String assetId, String userId) {
		String query = "";
		Favourite Favourite = null;
		try {
			query = "SELECT rt FROM Favourite rt where rt.assetId = '"  + assetId  + "' and rt.userProfile.userId = '" + userId + "'";  
			Query q = entityManager.createQuery(query);
			Favourite = (Favourite) q.getSingleResult();
		} catch (Exception e) {
			return Favourite;
		}

		return Favourite;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void deleteFirstFavouriteByUserId(String userId) {
		String query = "";
		Favourite Favourite = null;
		try {
			query = "SELECT rt FROM Favourite rt where rt.userProfile.userId = '" + userId + "'";  
			Query q = entityManager.createQuery(query);
			q.setFirstResult(0);
			q.setMaxResults(1);
			Favourite = (Favourite) q.getResultList().get(0);
			if(Favourite != null){
				entityManager.remove(Favourite);
				entityManager.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}

	}


	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean deleteFavouriteByUserIdWithAssetId(String assetId, String userId) {
		boolean retVal = true;
		try {
			Favourite Favourite = getFavouriteByUserIdWithAssetId(assetId, userId);
			if(Favourite != null){
				entityManager.remove(Favourite);
				entityManager.flush();
			}
		} catch (Exception e) {
		}
		return retVal;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<FavouriteVO> getFavouriteListByUserId(String userId) {
		List<FavouriteVO> FavouriteVOs = new ArrayList<FavouriteVO>();
		Query q = entityManager.createQuery(
				"SELECT rt FROM Favourite rt where rt.userProfile.userId = '" + userId + "'");  
		List<Favourite> Favourites = q.getResultList();
		if(Favourites != null && Favourites.size() > 0){
			for (Favourite Favourite : Favourites) {
				FavouriteVOs
				.add(FavouritetHelper.toFavouriteVO(Favourite));
			}
		}
		
		return FavouriteVOs;
	}
}
