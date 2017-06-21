package com.accedo.wynkstudio.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.accedo.wynkstudio.dao.BundleCounterDao;
import com.accedo.wynkstudio.entity.BundleCounter;
import com.accedo.wynkstudio.entity.Favourite;
import com.accedo.wynkstudio.helper.BundleCounterHelper;
import com.accedo.wynkstudio.helper.FavouritetHelper;
import com.accedo.wynkstudio.vo.BundleCounterVO;
import com.accedo.wynkstudio.vo.FavouriteVO;

/**
 * @author Accedo Software Private Limited
 * @version 1.0
 * @since 2014-07-01
 */
@Repository
public class BundleCounterDaoImpl implements BundleCounterDao  {

	private EntityManager entityManager;

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean createBundleCounter(BundleCounter bundleCounter) {
		boolean retVal = true;
		try {
			entityManager.persist(bundleCounter);
			entityManager.flush();
		} catch (Exception e) {
			return false;
		}
		return retVal;
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String updateBundleCounter(BundleCounter bundleCounter) {
		String result = "error"; 
		try {
			entityManager.merge(bundleCounter);
			entityManager.flush();
			result = "success";
		} catch (Exception e) {
			return e.getMessage();
		}
		return result;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean deleteBundleCounter(String bundleCounterId) {
		boolean result = false;
		try {
			Query q = entityManager.createQuery("delete from BundleCounter where id = " + bundleCounterId);
			q.executeUpdate();
			entityManager.flush();
			result = true;
		} catch (Exception e) {
			return result;
			
		}
		return result;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public BundleCounterVO getBundleCounterByUserWithProductId(String productId, String userId) {
		String query = "";
		BundleCounterVO bundleCounterVO = null;
		try {
			query = "SELECT bc FROM BundleCounter bc where bc.productId = '"  + productId  + "' and bc.userProfile.userId = '" + userId + "'";  
			Query q = entityManager.createQuery(query);
			BundleCounter bundleCounter = (BundleCounter) q.getSingleResult();
			if(bundleCounter != null){
				bundleCounterVO = BundleCounterHelper.toBundleCounterVO(bundleCounter);
			}
		} catch (Exception e) {
			return bundleCounterVO;
		}

		return bundleCounterVO;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public List<BundleCounterVO> getBundleCountersByUserId(String userId) {
		String query = "";
		List<BundleCounterVO> bundleCounterVOs = new ArrayList<BundleCounterVO>();
		try {
			query = "SELECT bc FROM BundleCounter bc where bc.userProfile.userId = '" + userId + "'";  
			Query q = entityManager.createQuery(query);
			List<BundleCounter> bundles = q.getResultList();
			if(bundles != null && bundles.size() > 0){
				for (BundleCounter bundle : bundles) {
					bundleCounterVOs.add(BundleCounterHelper.toBundleCounterVO(bundle));
				}
			}
		} catch (Exception e) {
			return bundleCounterVOs;
		}

		return bundleCounterVOs;
	}
	
	
}
