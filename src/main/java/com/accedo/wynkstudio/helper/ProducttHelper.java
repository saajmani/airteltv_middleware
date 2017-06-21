package com.accedo.wynkstudio.helper;

import java.util.ArrayList;
import java.util.List;

import com.accedo.wynkstudio.entity.Product;
import com.accedo.wynkstudio.entity.UserProfile;
import com.accedo.wynkstudio.vo.ProductVO;
import com.eclipsesource.json.JsonObject;

public class ProducttHelper {
	
	
	/* Get AppGrid Session Key */
	public static ProductVO toProductVO(Product product) {
		ProductVO productVO = new ProductVO();
		productVO.setProductId(product.getProductId());
		productVO.setCpId(product.getCpId());
		productVO.setId(String.valueOf(product.getId()));
		productVO.setState(product.getState());
		productVO.setSubscribeButtonState(product.isSubscribeButtonState());
		productVO.setUnsubscribeButtonState(product.isUnsubscribeButtonState());
		productVO.setAllowPlayback(product.isAllowPlayback());	
		productVO.setBundleFlag(product.getBundleFlag());
		productVO.setActive(product.getActive());
		productVO.setLive(product.getLive());
		productVO.setProductType(product.getProductType());
		productVO.setBsbValidity(product.getBsbValidity());
		productVO.setContentValidity(product.getContentValidity());
		productVO.setMessageTimeStamp(product.getMessageTimeStamp());
		productVO.setMessageFlag(product.isMessageFlag());
		
		return productVO;
	}
	
	public static Product toProduct(ProductVO productVO) {
		Product product = new Product();
		product.setProductId(productVO.getProductId());
		product.setCpId(productVO.getCpId());
		product.setState(productVO.getState());
		product.setSubscribeButtonState(productVO.isSubscribeButtonState());
		product.setUnsubscribeButtonState(productVO.isUnsubscribeButtonState());
		product.setAllowPlayback(productVO.isAllowPlayback());		
		product.setMessageFlag(productVO.isMessageFlag());
		product.setActive(productVO.getActive());
		product.setMessageTimeStamp(productVO.getMessageTimeStamp());
		product.setProductType(productVO.getProductType());
		product.setRenewal(productVO.isRenewal());
		product.setLive(productVO.getLive());
		product.setBundleFlag(productVO.getBundleFlag());
		product.setSubscribeButtonState(productVO.isSubscribeButtonState());
		product.setUnsubscribeButtonState(productVO.isUnsubscribeButtonState());
		product.setContentValidity(productVO.getContentValidity());
		product.setBsbValidity(productVO.getBsbValidity());
		return product;
	}
	
	public static List<Product> toProductList(List<ProductVO> productVOs, UserProfile userProfile) {
		List<Product> recents = new ArrayList<Product>();
		for (ProductVO productVO : productVOs) {
			Product product = toProduct(productVO);
			product.setUserProfile(userProfile);
			recents.add(product);
		}
		
		return recents;
	}
	
	public static Product toProductWithDiffernetFields(String userId, String productId, String cpId, String productType,
			Boolean bundleFlag, Long bsbValidity, Long contentValidity, JsonObject statusObject) {
		Boolean allowPlayback = true;
		Boolean unsubscribe = true;
		Boolean subscribe = false;
		String state = "Active";
		Product product = null;

	    if (statusObject != null) {
	    	allowPlayback = statusObject.get("allowPlayback").asBoolean();
	    	unsubscribe = statusObject.get("unsubscribe").asBoolean();
	    	subscribe = statusObject.get("subscribe").asBoolean();
	    	state = statusObject.get("status").asString();
	    }
	    product = new Product();
	    product.setAllowPlayback(allowPlayback);
	    product.setCpId(cpId.toUpperCase());
	    product.setProductId(productId);
	    product.setState(state);
	    product.setSubscribeButtonState(subscribe);
	    product.setUnsubscribeButtonState(unsubscribe);
	    product.setActive(false);
	    product.setLive(true);
	    product.setBundleFlag(bundleFlag);
	    product.setBsbValidity(bsbValidity);
	    product.setContentValidity(contentValidity);
	    product.setProductType(productType);
	    UserProfile userProfile = new UserProfile();
	    userProfile.setUserId(userId);
		product.setUserProfile(userProfile );
		return product;
	}
	
}