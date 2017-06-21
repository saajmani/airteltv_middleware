package com.accedo.wynkstudio.dao.impl;

import java.util.ArrayList;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.accedo.wynkstudio.dao.UserFilterDao;
import com.accedo.wynkstudio.entity.Product;
import com.accedo.wynkstudio.entity.UserFilter;
import com.accedo.wynkstudio.helper.UserFilterHelper;
import com.accedo.wynkstudio.vo.UserFilterVO;

@Repository
public class UserFilterDaoImpl implements UserFilterDao {

	private EntityManager entityManager;

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String createUserFilter(UserFilter userFilter) {
		String retVal = "Success";
		try {
			entityManager.persist(userFilter);
			entityManager.flush();
		} catch (Exception e) {
			retVal = "{\"message\":\"Duplicate\"}";
		}
		return retVal;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String deleteUserFilter(String uId, String filterName) {
		String query = "";
		String result = null;
		UserFilter userFilter = null;
		try {
			query = "SELECT uf FROM UserFilter uf where uf.userProfile.userId = '" + uId + "'" + " and uf.filterName = '" + filterName
					+ "'";
			Query q = entityManager.createQuery(query);
			q.setFirstResult(0);
			q.setMaxResults(1);
			userFilter = (UserFilter) q.getResultList().get(0);
			if (userFilter != null) {
				entityManager.remove(userFilter);
				entityManager.flush();
				result = "{\"message\":\"Deleted Successfully\"}";
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return result;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String updateUserFilter(String uId, String filterName, String oldName) {
		String success = "Updated successfully";
		try {
			Query q = entityManager
					.createQuery("update from UserFilter uf set uf.filterName='" + filterName + "' where uf.userProfile.userId = '"
							+ uId + "' and uf.filterName='" + oldName + "'" );
			q.executeUpdate();
//			entityManager.flush();
		} catch (Exception e) {
			return ("Error Occured : Duplicate name" + e.getMessage());
		}
		return success;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<UserFilterVO> getUserFilterUId(String uId) {
		List<UserFilterVO> userFilterVOs = new ArrayList<UserFilterVO>();
		Query q = entityManager.createQuery("SELECT uf FROM UserFilter uf where uf.userProfile.userId = '" + uId + "' order by id desc");
		List<UserFilter> userfilters = q.getResultList();
		if (userfilters != null && userfilters.size() > 0) {
			for (UserFilter userfilter : userfilters) {
				userFilterVOs.add(UserFilterHelper.toUserFilterVO(userfilter));
			}
		}

		return userFilterVOs;
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int getUserFilterCountByUserId(String userId, String filtertype) {
		int size = 0;
		long count = 0;
		Query q = entityManager.createQuery(
				"SELECT count(uf.id) FROM UserFilter uf where uf.userProfile.userId = '" + userId + "' and uf.filterType='" + filtertype + "'");  
		count = (long) q.getSingleResult();
		size = (int) count;
		return size;
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int getUserFilterNameCount(String userId, String filterName, String filtertype) {
		int size = 0;
		long count = 0;
		Query q = entityManager.createQuery(
				"SELECT count(uf.id) FROM UserFilter uf where  uf.filterName='" + filterName + "' and uf.userProfile.userId = '" + userId + "' and uf.filterType='" + filtertype + "'");  
		count = (long) q.getSingleResult();
		size = (int) count;
		return size;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public UserFilterVO getUserFilterByFilterDetails(String uId,  String filter, String filtertype, String sortingKey, String languages) {
		UserFilterVO userFilterVO = null;
		try {
			
		
		String qlString = "SELECT uf FROM UserFilter uf where uf.userProfile.userId = '" + uId +
				"' and uf.filter='" + filter + "' and uf.filterType='" + filtertype + "' and uf.sortingKey='" + sortingKey + "' and uf.languages='" + languages + "'" ;
		Query q = entityManager.createQuery(qlString);
		UserFilter userFilter = (UserFilter) q.getResultList().get(0);
		if (userFilter != null) {
				 userFilterVO =  UserFilterHelper.toUserFilterVO(userFilter);
			}
		} catch (Exception e) {
			return null;
		}
		return userFilterVO;
	}
}
