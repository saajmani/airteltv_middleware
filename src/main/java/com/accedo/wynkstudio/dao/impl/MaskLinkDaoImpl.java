package com.accedo.wynkstudio.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.accedo.wynkstudio.dao.MaskLinkDao;
import com.accedo.wynkstudio.entity.MaskLink;
import com.accedo.wynkstudio.helper.MaskLinkHelper;
import com.accedo.wynkstudio.vo.MaskLinkVO;

/**
 * @author Accedo Software Private Limited
 * @version 1.0
 * @since 2014-07-01
 */
@Repository
public class MaskLinkDaoImpl implements MaskLinkDao {

	private EntityManager entityManager;

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean create(MaskLink maskLink) {
		boolean retVal = true;
		try {
			entityManager.persist(maskLink);
			entityManager.flush();
		} catch (Exception e) {
		}
		return retVal;
	}

	

	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public MaskLinkVO getByHashCode(String hashCode) {
		String query = "";
		MaskLinkVO maskLinkVO = null;
		try {
			query = "SELECT ml FROM MaskLink ml where ml.hashCode = '"  + hashCode  + "'";  
			Query q = entityManager.createQuery(query);
			MaskLink maskLink = (MaskLink) q.getSingleResult();
			if(maskLink != null){
				maskLinkVO = MaskLinkHelper.toMaskLinkVO(maskLink);
			}
		} catch (Exception e) {
			return maskLinkVO;
		}

		return maskLinkVO;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public MaskLinkVO getByUrl(String url) {
		String query = "";
		MaskLinkVO maskLinkVO = null;
		try {
			query = "SELECT ml FROM MaskLink ml where ml.url = '"  + url  + "'";  
			Query q = entityManager.createQuery(query);
			MaskLink maskLink = (MaskLink) q.getSingleResult();
			if(maskLink != null){
				maskLinkVO = MaskLinkHelper.toMaskLinkVO(maskLink);
			}
		} catch (Exception e) {
			return maskLinkVO;
		}

		return maskLinkVO;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<MaskLinkVO> getList() {
		List<MaskLinkVO> maskLinkVOs = new ArrayList<MaskLinkVO>();
		Query q = entityManager.createQuery(
				"SELECT ml FROM MaskLink ml ");  
		List<MaskLink> maskLinks = q.getResultList();
		if(maskLinks != null && maskLinks.size() > 0){
			for (MaskLink maskLink : maskLinks) {
				maskLinkVOs.add(MaskLinkHelper.toMaskLinkVO(maskLink));
			}
		}
		
		return maskLinkVOs;
	}
}
