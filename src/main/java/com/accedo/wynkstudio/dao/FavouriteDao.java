package com.accedo.wynkstudio.dao;

import java.util.List;

import com.accedo.wynkstudio.entity.Favourite;
import com.accedo.wynkstudio.vo.FavouriteVO;

public interface FavouriteDao {

	public boolean createFavourite(Favourite favourite);
	
	public boolean deleteFavouriteByUserIdWithAssetId(String assetId, String userId);
	
	public List<FavouriteVO> getFavouriteListByUserId(String userId);
	
	public boolean createFavouriteList(List<Favourite> favourites);
	
}
