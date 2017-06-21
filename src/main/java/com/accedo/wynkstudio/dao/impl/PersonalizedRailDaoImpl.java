package com.accedo.wynkstudio.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.accedo.wynkstudio.dao.PersonalizedRailDao;
import com.accedo.wynkstudio.entity.PersonalizedRail;
import com.accedo.wynkstudio.helper.PersonalizedRailHelper;
import com.accedo.wynkstudio.vo.PersonalizedRailVO;


/**
 * @author Accedo Software Private Limited
 * @version 1.0
 * @since 2014-07-01
 */
@Repository
public class PersonalizedRailDaoImpl implements PersonalizedRailDao  {

	private EntityManager entityManager;

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean createPersonalizedRail(PersonalizedRail personalizedRail) {
		boolean retVal = true;
		try {
			entityManager.persist(personalizedRail);
			entityManager.flush();
		} catch (Exception e) {
			return false;
		}
		return retVal;
	}


	public PersonalizedRailVO getPersonalizedRailByUserIdWithRailType(String userId, String railType) {
		PersonalizedRailVO personalizedRailVO = null;
		try {
			String query = "SELECT pr from PersonalizedRail pr where pr.userProfile.userId = '"  + userId  + "' and pr.railType = '" + railType + "'";  
			Query q = entityManager.createQuery(query);
			PersonalizedRail personalizedRail = (PersonalizedRail) q.getResultList().get(0);
			if(personalizedRail != null){
				personalizedRailVO = PersonalizedRailHelper.toPersonalizedRailVO(personalizedRail);
			}
		} catch (Exception e) {
			return personalizedRailVO;
		}

		return personalizedRailVO;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean updatePersonalizedRail(PersonalizedRail personalizedRail) {
		boolean result = false; 
		try {
			entityManager.merge(personalizedRail);
			entityManager.flush();
			result = true;
		} catch (Exception e) {
			return result;
		}
		return result;
	}
}
