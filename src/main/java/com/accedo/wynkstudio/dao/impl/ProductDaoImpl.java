package com.accedo.wynkstudio.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.accedo.wynkstudio.dao.ProductDao;
import com.accedo.wynkstudio.entity.Product;
import com.accedo.wynkstudio.helper.ProducttHelper;
import com.accedo.wynkstudio.vo.ProductVO;

/**
 * @author Accedo Software Private Limited
 * @version 1.0
 * @since 2014-07-01
 */
@Repository
public class ProductDaoImpl implements ProductDao  {

	private EntityManager entityManager;

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean createProduct(Product product) {
		boolean retVal = true;
		try {
			entityManager.persist(product);
			entityManager.flush();
		} catch (Exception e) {
		}
		return retVal;
	}
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean updateProduct(Product product) {
		boolean result = false; 
		try {
			entityManager.merge(product);
			entityManager.flush();
			result = true;
		} catch (Exception e) {
			return result;
		}
		return result;
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean productSubscription(Product product) {
		boolean retVal = true;
		try {
			entityManager.persist(product);
			entityManager.flush();
		} catch (Exception e) {
		}
		return retVal;
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean productUnSubscription(Product product) {
		boolean retVal = true;
		try {
			entityManager.merge(product);
			entityManager.flush();
		} catch (Exception e) {
		}
		return retVal;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public ProductVO getProductByUserWithProductId(String productId, String userId) {
		String query = "";
		ProductVO productVO = null;
		try {
			query = "SELECT pt FROM Product pt where pt.productId = '"  + productId  + "' and pt.userProfile.userId = '" + userId + "'";  
			Query q = entityManager.createQuery(query);
			Product product = (Product) q.getResultList().get(0);
			if(product != null){
				productVO = ProducttHelper.toProductVO(product);
			}
		} catch (Exception e) {
			return productVO;
		}

		return productVO;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean deleteProduct(Product product) {
		boolean result = false;
		try {
			Query q = entityManager.createQuery("delete from Product where id = " + product.getId());
			q.executeUpdate();
			entityManager.flush();
			result = true;
		} catch (Exception e) {
			return result;
			
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<String> getProductIdsByUserId(String userId) {
		String query = "";
		List<String> productIds = null;
		try {
			query = "SELECT pt.productId FROM Product pt where pt.userProfile.userId = '" + userId + "'";  
			Query q = entityManager.createQuery(query);
			productIds = q.getResultList();
		} catch (Exception e) {
			return productIds;
		}

		return productIds;
	}
	
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<ProductVO> getProductsByUserId(String userId) {
		String query = "";
		List<Product> products = null;
		List<ProductVO> ProductVOs = new ArrayList<ProductVO>();
		try {
			query = "SELECT pt FROM Product pt where pt.userProfile.userId = '" + userId + "'";  
			Query q = entityManager.createQuery(query);
			products = q.getResultList();
			if(products != null && products.size() > 0){
				for (Product product : products) {
					ProductVOs.add(ProducttHelper.toProductVO(product));
				}
			}
		} catch (Exception e) {
			return ProductVOs;
		}

		return ProductVOs;
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean updateProductLiv(String userId, String cpId, boolean live) {
		try {
				Query q = entityManager.createQuery("update from Product pt set live= " + live + " where pt.userProfile.userId = '" + userId + "' and cpId='" + cpId + "'");
				q.executeUpdate();
				entityManager.flush();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean updateProductActive(String userId, String productId, boolean active) {
		try {
				Query q = entityManager.createQuery("update from Product pt set active= " + active + " where pt.userProfile.userId = '" + userId + "' and pt.productId = '"  + productId  + "'");
				q.executeUpdate();
				entityManager.flush();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean updateProductValidity(String userId, String productId, long validity) {
		try {
				Query q = entityManager.createQuery("update from Product pt set contentValidity= " + validity + " where pt.userProfile.userId = '" + userId + "' and pt.productId = '"  + productId  + "'");
				q.executeUpdate();
				entityManager.flush();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean updateProductBsbValidity(String userId, String productId, long validity) {
		try {
				Query q = entityManager.createQuery("update from Product pt set bsbValidity= " + validity + " where pt.userProfile.userId = '" + userId + "' and pt.productId = '"  + productId  + "'");
				q.executeUpdate();
				entityManager.flush();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean updateProductActivated(String userId, String productId) {
		try {
				Query q = entityManager.createQuery("update from Product pt set activated= '" + new Timestamp(new java.util.Date().getTime()) + "' where pt.userProfile.userId = '" + userId + "' and pt.productId = '"  + productId  + "'");
				q.executeUpdate();
				entityManager.flush();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean updateProductRenewal(String userId, String productId) {
		try {
				Query q = entityManager.createQuery("update from Product pt set renewal= true where pt.userProfile.userId = '" + userId + "' and pt.productId = '"  + productId  + "'");
				q.executeUpdate();
				entityManager.flush();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean updateProductMessageFlag(String userId, String productId, boolean messageFlag, long timestamp) {
		try {
				Query q = entityManager.createQuery("update from Product pt set messageFlag= " + messageFlag + ", messageTimeStamp=" + timestamp  +"where pt.userProfile.userId = '" + userId + "' and pt.productId = '"  + productId  + "'");
				q.executeUpdate();
				entityManager.flush();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<ProductVO> getActiveProductByUserId(String userId) {
		String query = "";
		List<Product> products = null;
		List<ProductVO> ProductVOs = new ArrayList<ProductVO>();
		try {
			query = "SELECT pt FROM Product pt where pt.userProfile.userId = '" + userId + "' and pt.active=true";  
			Query q = entityManager.createQuery(query);
			products = q.getResultList();
			if(products != null && products.size() > 0){
				for (Product product : products) {
					ProductVOs.add(ProducttHelper.toProductVO(product));
				}
			}
		} catch (Exception e) {
			return ProductVOs;
		}

		return ProductVOs;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean getActiveProductByUserIdAndProductId(String userId,String productId) {
		String query = "";
		List<Product> products = null;
		ProductVO ProductVO = new ProductVO();
		boolean result = false;
		try {
			query = "SELECT pt FROM Product pt where pt.userProfile.userId = '" + userId + "' and pt.active=true and pt.productId = '" + productId+ "'";  
			Query q = entityManager.createQuery(query);
			products = q.getResultList();
			if(products != null && products.size() > 0){
				result = false;
			}else{
				result = true;
			}
			
		} catch (Exception e) {
			return result;
		}

		return result;
	}
	
}
