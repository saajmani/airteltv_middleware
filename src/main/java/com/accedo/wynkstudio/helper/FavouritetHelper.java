package com.accedo.wynkstudio.helper;

import java.util.ArrayList;
import java.util.List;

import com.accedo.wynkstudio.entity.Favourite;
import com.accedo.wynkstudio.entity.UserProfile;
import com.accedo.wynkstudio.vo.FavouriteVO;

public class FavouritetHelper {
	
	
	/* Get AppGrid Session Key */
	public static FavouriteVO toFavouriteVO(Favourite favourite) {
		FavouriteVO favouriteVO = new FavouriteVO();
		favouriteVO.setAssetId(favourite.getAssetId());
		favouriteVO.setCpToken(favourite.getCpToken());
		favouriteVO.setDownloadedDate(favourite.getDownloadedDate());
		favouriteVO.setDuration(favourite.getDuration());
		favouriteVO.setLastWatchedPosition(favourite.getLastWatchedPosition());
		favouriteVO.setLastWatchedTime(favourite.getLastWatchedTime());
		return favouriteVO;
	}
	
	public static Favourite toFavourite(FavouriteVO favouriteVO) {
		Favourite favourite = new Favourite();
		if(favouriteVO.getAssetId() != null){
			favourite.setAssetId(favouriteVO.getAssetId());
			favourite.setCpToken(favouriteVO.getCpToken());
			favourite.setDownloadedDate(favouriteVO.getDownloadedDate());
			favourite.setDuration(favouriteVO.getDuration());
			favourite.setLastWatchedPosition(favouriteVO.getLastWatchedPosition());
			favourite.setLastWatchedTime(favouriteVO.getLastWatchedTime());
		}
		return favourite;
	}
	
	public static List<Favourite> toFavouriteList(List<FavouriteVO> favouriteVOs, UserProfile userProfile) {
		List<Favourite> recents = new ArrayList<Favourite>();
		for (FavouriteVO favouriteVO : favouriteVOs) {
			Favourite favourite = toFavourite(favouriteVO);
			favourite.setUserProfile(userProfile);
			recents.add(favourite);
		}
		
		return recents;
	}
	
	
}