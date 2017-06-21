package com.accedo.wynkstudio.helper;

import java.util.ArrayList;
import java.util.List;

import com.accedo.wynkstudio.entity.BundleCounter;
import com.accedo.wynkstudio.entity.Favourite;
import com.accedo.wynkstudio.entity.Product;
import com.accedo.wynkstudio.entity.Recent;
import com.accedo.wynkstudio.entity.UserProfile;
import com.accedo.wynkstudio.vo.BundleCounterVO;
import com.accedo.wynkstudio.vo.FavouriteVO;
import com.accedo.wynkstudio.vo.ProductVO;
import com.accedo.wynkstudio.vo.RecentVO;
import com.accedo.wynkstudio.vo.UserProfileVO;

public class UserProfileHelper {
	
	
	/* Get AppGrid Session Key */
	public static UserProfileVO toUserProfileVO(UserProfile userProfile) {
		UserProfileVO userProfileVO = new UserProfileVO();
		userProfileVO.setCpToken(userProfile.getCpToken() != null ? userProfile.getCpToken() : "");
		userProfileVO.setDob(userProfile.getDateOfBirth());
		userProfileVO.setEmail(userProfile.getEmail());
		userProfileVO.setGender(userProfile.getGender());
		userProfileVO.setMpxToken(userProfile.getMpxToken() != null ? userProfile.getMpxToken() : "");
		userProfileVO.setName(userProfile.getName());
		userProfileVO.setUserId(userProfile.getUserId());
		userProfileVO.setIsCreatedFlag(userProfile.isCreatedFlag());
		userProfileVO.setIsSubscribedFlag(userProfile.isSubscribedFlag());
		userProfileVO.setRegisteredChannels(userProfile.getRegisteredChannels());
		List<FavouriteVO> favoriteMovies = new ArrayList<FavouriteVO>();
		List<RecentVO> lastWatchedMovies = new ArrayList<RecentVO>();
		if(userProfile.getFavourites() != null && userProfile.getFavourites().size() > 0){
			FavouriteVO favouriteVO = null;
			for (Favourite favourite : userProfile.getFavourites()) {
				favouriteVO = new FavouriteVO();
				favouriteVO.setAssetId(favourite.getAssetId());
				favouriteVO.setCpToken(favourite.getCpToken());
				favouriteVO.setDownloadedDate(favourite.getDownloadedDate());
				favouriteVO.setDuration(favourite.getDuration());
				favouriteVO.setLastWatchedPosition(favourite.getLastWatchedPosition());
				favouriteVO.setLastWatchedTime(favourite.getLastWatchedTime());
				favoriteMovies.add(favouriteVO);
			}
		}
		userProfileVO.setFavoriteMovies(favoriteMovies);
		if(userProfile.getRecents() != null && userProfile.getRecents().size() > 0){
			RecentVO recentVO = null;
			for (Recent recent : userProfile.getRecents()) {
				recentVO = new RecentVO();
				recentVO.setAssetId(recent.getAssetId());
				recentVO.setCpToken(recent.getCpToken());
				recentVO.setDownloadedDate(recent.getDownloadedDate());
				recentVO.setDuration(recent.getDuration());
				recentVO.setLastWatchedPosition(recent.getLastWatchedPosition());
				recentVO.setLastWatchedTime(recent.getLastWatchedTime());
				lastWatchedMovies.add(recentVO);
			}
		}
		userProfileVO.setLastWatchedMovies(lastWatchedMovies);
		List<ProductVO> subscribedChannels = new ArrayList<ProductVO>();
		if(userProfile.getProduct() != null && userProfile.getProduct().size() > 0){
			for (Product product : userProfile.getProduct()) {
				subscribedChannels.add(ProducttHelper.toProductVO(product));
			}
		}
		userProfileVO.setSubscribedChannels(subscribedChannels);
		
		List<BundleCounterVO> bundleCounter = new ArrayList<BundleCounterVO>();
		if(userProfile.getBundleCounters() != null && userProfile.getBundleCounters().size() > 0){
			BundleCounterVO bundleCounterVO = null;
			for (BundleCounter bundleCount : userProfile.getBundleCounters()) {
				bundleCounterVO = new BundleCounterVO();
				bundleCounterVO.setProductId(bundleCount.getProductId());
				bundleCounterVO.setCpId(bundleCount.getCpId());
				bundleCounterVO.setId(String.valueOf(bundleCount.getId()));
				bundleCounterVO.setItemLimit(bundleCount.getItemLimit());
				bundleCounterVO.setCounter(bundleCount.getCounter());
				bundleCounterVO.setMediaList(bundleCount.getMediaList());
				bundleCounter.add(bundleCounterVO);
			}
		}
		userProfileVO.setBundleCounter(bundleCounter);
		
		
		return userProfileVO;
	}
	
}