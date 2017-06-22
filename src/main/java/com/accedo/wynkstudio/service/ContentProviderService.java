package com.accedo.wynkstudio.service;

public interface ContentProviderService {

	public String getProgramsByCategoryWithType(String cpId, String categoryId, String programType,String order, String sortingKey, String totalPageSize, String pageNumber, String pageSize);
	
	public String getProgramById(String cpId, String videoId);
	
	public String getSeasonById(String cpId, String id, String totalPageSize, String pageSize, String pageNumber);
	
	public String getBanner(String cpId);

	public String getSeasonBySeriesId(String cpId, String showId, String totalPageSize, String pageSize, String pageNumber);

	public String getRelatedProgramById(String cpId,String guid);

	public String getSortingOptions(String cpToken, String language);

	public String getNextEpisode(String cpToken, String episodeId, String seriesId, String seasonNumber, String episodeNumber,
			String tvSeasonId, Boolean seriesFlag);

	public byte[] getAssets(String dpi);
	
	public byte[] getAssetsForNewVersion(String dpi);
	
	public String updateAssets();
	
	public String refreshAssets();

    public String getSubscriptionPlans(String cpId, String platform, String appVersion, String deviceId, String uid,
            String token);

	public String getAllAssets();
}
