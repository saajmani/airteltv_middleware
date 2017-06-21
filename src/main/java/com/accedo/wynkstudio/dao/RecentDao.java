package com.accedo.wynkstudio.dao;

import java.util.List;

import com.accedo.wynkstudio.entity.Recent;
import com.accedo.wynkstudio.vo.RecentVO;

public interface RecentDao {

	public boolean createRecent(Recent recent);
	
	public boolean deleteRecentByUserIdWithAssetId(String assetId, String userId);
	
	public int getRecentListCountByUserId(String userId);
	
	public void deleteFirstRecentByUserId(String userId);
	
	public List<RecentVO> getRecentListUserId(String userId);
	
	public boolean createRecent(List<Recent> recents);
	
	
	
}
