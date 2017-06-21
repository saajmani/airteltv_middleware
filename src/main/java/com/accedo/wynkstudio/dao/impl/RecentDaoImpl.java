package com.accedo.wynkstudio.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.accedo.wynkstudio.dao.RecentDao;
import com.accedo.wynkstudio.entity.Recent;
import com.accedo.wynkstudio.helper.RecentHelper;
import com.accedo.wynkstudio.vo.RecentVO;

/**
 * @author Accedo Software Private Limited
 * @version 1.0
 * @since 2014-07-01
 */
@Repository
public class RecentDaoImpl implements RecentDao {

	private EntityManager entityManager;

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean createRecent(Recent recent) {
		boolean retVal = true;
		try {
			entityManager.persist(recent);
			entityManager.flush();
		} catch (Exception e) {
		}
		return retVal;
	}

	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean createRecent(List<Recent> recents) {
		boolean retVal = true;
		try {
			entityManager.merge(recents);
			entityManager.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retVal;
	}

	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	private List<Recent> getRecentByUserIdWithAssetId(String assetId, String userId) {
		String query = "";
		List<Recent> recent = null;
		try {
			query = "SELECT rt FROM Recent rt where rt.assetId = '"  + assetId  + "' and rt.userProfile.userId = '" + userId + "'";  
			Query q = entityManager.createQuery(query);
//			recent = (Recent) q.getSingleResult();
			recent = q.getResultList();
		} catch (Exception e) {
			return recent;
		}

		return recent;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void deleteFirstRecentByUserId(String userId) {
		String query = "";
		Recent recent = null;
		try {
			query = "SELECT rt FROM Recent rt where rt.userProfile.userId = '" + userId + "'";  
			Query q = entityManager.createQuery(query);
			q.setFirstResult(0);
			q.setMaxResults(1);
			recent = (Recent) q.getResultList().get(0);
			if(recent != null){
				entityManager.remove(recent);
				entityManager.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}

	}


	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean deleteRecentByUserIdWithAssetId(String assetId, String userId) {
		boolean retVal = true;
		List<Recent> recentList = null;
		try {
			recentList = getRecentByUserIdWithAssetId(assetId, userId);
			if(recentList != null){
				for(int i=0;i<recentList.size();i++){
				entityManager.remove(recentList.get(i));
				entityManager.flush();
				}
			}
		} catch (Exception e) {
		}
		return retVal;
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int getRecentListCountByUserId(String userId) {
		int size = 0;
		long count = 0;
		Query q = entityManager.createQuery(
				"SELECT count(rt.id) FROM Recent rt where rt.userProfile.userId = '" + userId + "'");  
		count = (long) q.getSingleResult();
		size = (int) count;
		return size;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<RecentVO> getRecentListUserId(String userId) {
		List<RecentVO> recentVOs = new ArrayList<RecentVO>();
		Query q = entityManager.createQuery(
				"SELECT rt FROM Recent rt where rt.userProfile.userId = '" + userId + "'");  
		List<Recent> recents = q.getResultList();
		if(recents != null && recents.size() > 0){
			for (Recent recent : recents) {
				recentVOs
				.add(RecentHelper.toRecentVO(recent));
			}
		}
		
		return recentVOs;
	}
}
