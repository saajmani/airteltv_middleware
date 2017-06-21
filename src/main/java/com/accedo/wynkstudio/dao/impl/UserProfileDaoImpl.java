package com.accedo.wynkstudio.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.accedo.wynkstudio.dao.UserProfileDao;
import com.accedo.wynkstudio.entity.BundleCounter;
import com.accedo.wynkstudio.entity.Favourite;
import com.accedo.wynkstudio.entity.Product;
import com.accedo.wynkstudio.entity.Recent;
import com.accedo.wynkstudio.entity.UserProfile;
import com.accedo.wynkstudio.helper.ProducttHelper;
import com.accedo.wynkstudio.helper.UserProfileHelper;
import com.accedo.wynkstudio.vo.ProductVO;
import com.accedo.wynkstudio.vo.UserProfileVO;

@Repository
public class UserProfileDaoImpl implements UserProfileDao{
	
	private EntityManager entityManager;

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String createUserProfile(UserProfile userProfile) {
		String result = "error"; 
		try {
			entityManager.persist(userProfile);
			entityManager.flush();
			result = "success";
		} catch (Exception e) {
			return e.getMessage();
		}
		return result;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String updateUserProfile(UserProfile userProfile) {
		String result = "error"; 
		try {
			Query q = entityManager.createQuery("update from UserProfile set "
					+ "gender= '" + userProfile.getGender() + "', "
					+ "name= '" + userProfile.getName() + "', "
					+ "email= '" + userProfile.getEmail() + "', "
					+ "dateOfBirth= '" + userProfile.getDateOfBirth() + "' where userId = '" + userProfile.getUserId() + "'");
			q.executeUpdate();
			result = "success";
		} catch (Exception e) {
			return e.getMessage();
		}
		return result;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public UserProfileVO getUserProfileByUserId(String userId) {
		UserProfile userProfile = null;
		UserProfileVO userProfileVO = null;
		try {
			userProfile = entityManager.find(UserProfile.class, userId);
			if (userProfile != null) {
				userProfileVO = UserProfileHelper.toUserProfileVO(userProfile);
			}
		} catch (Exception e) {
			return userProfileVO;
		}
		return userProfileVO;
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String updateUserLoginDetailse(String userId, String erosnowUserName, String erosnowUserToken, String erosnowUserTokenSecret) {
		String result = "error"; 
		try {
			Query q = entityManager.createQuery("update from UserProfile set "
					+ "erosnowUserName= '" + erosnowUserName + "', "
					+ "erosnowUserTokenSecret= '" + erosnowUserTokenSecret + "', "
					+ "erosnowUserToken= '" + erosnowUserToken + "' where userId = '" + userId + "'");
			q.executeUpdate();
			result = "success";
		} catch (Exception e) {
			return e.getMessage();
		}
		return result;
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public UserProfileVO getUserLoginDetails(String userId) {
		String query = "";
		UserProfileVO userProfileVO = null;
		UserProfile profile = null;
		try {
			query = "SELECT up FROM UserProfile up where up.userId = '" + userId + "'";
			Query q = entityManager.createQuery(query);
			profile = (UserProfile) q.getSingleResult();
			if (profile != null) {
				userProfileVO = new UserProfileVO();
				userProfileVO
						.setErosnowUserName(profile.getErosnowUserName() != null ? profile.getErosnowUserName() : "");
				userProfileVO.setErosnowUserToken(
						profile.getErosnowUserToken() != null ? profile.getErosnowUserToken() : "");
				userProfileVO.setErosnowUserTokenSecret(
						profile.getErosnowUserTokenSecret() != null ? profile.getErosnowUserTokenSecret() : "");
				userProfileVO.setEmail(profile.getEmail() != null ? profile.getEmail() : "");
				if (profile.getProduct() != null && profile.getProduct().size() > 0) {
					List<ProductVO> subscribedChannels = new ArrayList<>();
					for (Product product : profile.getProduct()) {
						if (!product.getState().equalsIgnoreCase("deactivated")) {
							subscribedChannels.add(ProducttHelper.toProductVO(product));
							userProfileVO.setSubscribedChannels(subscribedChannels);
						}
					}
				}
			}
		} catch (Exception e) {
			return userProfileVO;
		}
		return userProfileVO;
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean updateUserProfileCreatedflag(String userId, boolean createdFlag) {
		try {
				Query q = entityManager.createQuery("update from UserProfile set isCreatedFlag= " + createdFlag + ", isSubscribedFlag= " + createdFlag + " where userId = '" + userId + "'");
				q.executeUpdate();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean getHooqTrialFlag(String userId) {
		boolean trialFlag = false;
		try {
			String query = "";
			UserProfile profile = null;
			query = "SELECT up FROM UserProfile up where up.userId = '" + userId + "'";
			Query q = entityManager.createQuery(query);
			profile = (UserProfile) q.getSingleResult();
			trialFlag = profile.getHooqTrialFlag();
		} catch (Exception e) {
			return false;
		}
		return trialFlag;
	}

	@Override
	public boolean setHooqTrialFlag(String userId) {
		try {
			Query q = entityManager.createQuery("update from UserProfile set hooqTrialFlag= " + true
					+ " where userId = '" + userId + "'");
			q.executeUpdate();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	@Override
	public boolean getHooqTrialFlagIos(String userId) {
		boolean trialFlag = false;
		try {
			String query = "";
			UserProfile profile = null;
			query = "SELECT up FROM UserProfile up where up.userId = '" + userId + "'";
			Query q = entityManager.createQuery(query);
			profile = (UserProfile) q.getSingleResult();
			trialFlag = profile.getHooqTrialFlagIos();
		} catch (Exception e) {
			return false;
		}
		return trialFlag;
	}

	@Override
	public boolean setHooqTrialFlagIos(String userId) {
		try {
			Query q = entityManager.createQuery("update from UserProfile set hooqTrialFlagIos= " + true
					+ " where userId = '" + userId + "'");
			q.executeUpdate();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
