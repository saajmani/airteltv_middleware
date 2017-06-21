package com.accedo.wynkstudio.dao;

import java.util.List;

import com.accedo.wynkstudio.entity.Product;
import com.accedo.wynkstudio.vo.ProductVO;

public interface ProductDao {

	public boolean createProduct(Product product);
	
	public boolean productSubscription(Product product);
	
	public boolean productUnSubscription(Product product);
	
	public ProductVO getProductByUserWithProductId(String productId, String userId);
	
	public boolean deleteProduct(Product product);
	
	public List<String> getProductIdsByUserId(String userId);
	
	public List<ProductVO> getProductsByUserId(String userId);
	
	public boolean updateProductLiv(String userId, String cpId, boolean live);
	
	public boolean updateProductActive(String userId, String productId, boolean active);

	public boolean updateProductValidity(String userId, String productId, long validity);
	
	public boolean updateProductActivated(String userId, String productId);

	public boolean updateProductRenewal(String userId, String productId);
	
	public boolean updateProductMessageFlag(String userId, String productId, boolean messageFlag, long timestamp);
	
	public boolean updateProduct(Product product);
	
	public List<ProductVO> getActiveProductByUserId(String userId);

	boolean updateProductBsbValidity(String userId, String productId, long validity);
	
	public boolean getActiveProductByUserIdAndProductId(String userId,String productId);
	 
}
