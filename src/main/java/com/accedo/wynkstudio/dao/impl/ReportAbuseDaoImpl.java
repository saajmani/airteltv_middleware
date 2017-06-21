package com.accedo.wynkstudio.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.accedo.wynkstudio.dao.ReportAbuseDao;
import com.accedo.wynkstudio.entity.Recent;
import com.accedo.wynkstudio.entity.ReportAbuse;

/**
 * @author Accedo Software Private Limited
 * @version 1.0
 * @since 2014-07-01
 */
@Repository
public class ReportAbuseDaoImpl implements ReportAbuseDao {

	private EntityManager entityManager;

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String createReportAbuse(ReportAbuse reportAbuse) {
		try {
			entityManager.persist(reportAbuse);
			entityManager.flush();
		} catch (Exception e) {
			return "success";
		}
		return "success";
	}

	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public ReportAbuse getReportAbuseByUserIdWithContentId(String contentId, String userId) {
		String query = "";
		ReportAbuse reportAbuse = null;
		try {
			query = "SELECT rt FROM ReportAbuse rt where rt.contentId = '"  + contentId  + "' and rt.userProfile.userId = '" + userId + "'";  
			Query q = entityManager.createQuery(query);
			reportAbuse = (ReportAbuse) q.getSingleResult();
		} catch (Exception e) {
			return reportAbuse;
		}

		return reportAbuse;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void deleteFirstReportAbuseByUserId(String userId) {
		String query = "";
		ReportAbuse reportAbuse = null;
		try {
			query = "SELECT rt FROM ReportAbuse rt where rt.userProfile.userId = '" + userId + "'";  
			Query q = entityManager.createQuery(query);
			q.setFirstResult(0);
			q.setMaxResults(1);
			reportAbuse = (ReportAbuse) q.getResultList().get(0);
			if(reportAbuse != null){
				entityManager.remove(reportAbuse);
				entityManager.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}

	}


	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean deleteReportAbuseByUserIdWithContentId(String contentId, String userId) {
		boolean retVal = true;
		try {
			ReportAbuse reportAbuse = getReportAbuseByUserIdWithContentId(contentId, userId);
			if(reportAbuse != null){
				entityManager.remove(reportAbuse);
				entityManager.flush();
			}
		} catch (Exception e) {
		}
		return retVal;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public List<ReportAbuse> listAbuse(String contentId) {
		Query q = entityManager.createQuery(
				"SELECT rt FROM ReportAbuse rt where rt.contentId = '"  + contentId  + "'");  
		List<ReportAbuse> reportAbuses = q.getResultList();
		return reportAbuses;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public List<?> summaryOfReportAbuse() {
		// TODO Auto-generated method stub
		Query q = entityManager.createNativeQuery("select contentId, cpId, count(contentId) from user_reportabuse group by contentId order by count(contentId) desc");
		 List resultList = q.getResultList();
		return resultList;
	}
	
}
