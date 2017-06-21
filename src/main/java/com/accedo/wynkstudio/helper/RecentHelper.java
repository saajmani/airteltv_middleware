package com.accedo.wynkstudio.helper;

import java.util.ArrayList;
import java.util.List;

import com.accedo.wynkstudio.entity.Recent;
import com.accedo.wynkstudio.entity.UserProfile;
import com.accedo.wynkstudio.vo.RecentVO;

public class RecentHelper {
	
	
	/* Get AppGrid Session Key */
	public static RecentVO toRecentVO(Recent recent) {
		RecentVO recentVO = new RecentVO();
		recentVO.setAssetId(recent.getAssetId());
		recentVO.setCpToken(recent.getCpToken());
		recentVO.setDownloadedDate(recent.getDownloadedDate());
		recentVO.setDuration(recent.getDuration());
		recentVO.setLastWatchedPosition(recent.getLastWatchedPosition());
		recentVO.setLastWatchedTime(recent.getLastWatchedTime());
		return recentVO;
	}
	
	public static Recent toRecent(RecentVO recentVO) {
		Recent recent = new Recent();
		if (recentVO.getAssetId() != null) {
			recent.setAssetId(recentVO.getAssetId());
			recent.setCpToken(recentVO.getCpToken());
			recent.setDownloadedDate(recentVO.getDownloadedDate());
			recent.setDuration(recentVO.getDuration());
			recent.setLastWatchedPosition(recentVO.getLastWatchedPosition());
			recent.setLastWatchedTime(recentVO.getLastWatchedTime());
		}
		return recent;
	}
	
	public static List<Recent> toRecentList(List<RecentVO> recentVOs, UserProfile userProfile) {
		List<Recent> recents = new ArrayList<Recent>();
		for (RecentVO recentVO : recentVOs) {
			Recent recent = toRecent(recentVO);
			recent.setUserProfile(userProfile);
			recents.add(recent);
		}
		
		return recents;
	}
	
	
	
	
	
}