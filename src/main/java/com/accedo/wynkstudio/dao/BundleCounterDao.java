package com.accedo.wynkstudio.dao;

import java.util.List;

import com.accedo.wynkstudio.entity.BundleCounter;
import com.accedo.wynkstudio.vo.BundleCounterVO;

public interface BundleCounterDao {

	public boolean createBundleCounter(BundleCounter bundleCounter);
	
	public String updateBundleCounter(BundleCounter bundleCounter);
	
	public boolean deleteBundleCounter(String bundleCounterId);
	
	public BundleCounterVO getBundleCounterByUserWithProductId(String productId, String userId);

	public List<BundleCounterVO> getBundleCountersByUserId(String userId);
}
