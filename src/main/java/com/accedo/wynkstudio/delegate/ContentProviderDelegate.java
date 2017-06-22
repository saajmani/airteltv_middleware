package com.accedo.wynkstudio.delegate;

/**
 * 
 * @author Accedo
 * 
 *
 */
public interface ContentProviderDelegate {
	/**
	 * 
	 * @param message
	 * @return response {@link String}
	 */
	public String getProgramsByCategoryWithType(String cpId, String categoryId, String programType,String order, String sortingKey, String totalPageSize, String pageNumber, String pageSize);

	public String getProgramById(String cpId, String videoId);

	public String getSeasonById(String cpId, String id, String totalPageSize, String pageSize, String pageNumber);
	
	public String getBanner(String cpId);

	public String getSeasonBySeriesId(String cpToken, String showId, String totalPageSize, String pageSize, String pageNumber);
	
	public String getRelatedProgramById(String cpToken, String guid);

	public String getSortingOptions(String cpToken, String language);

	public String getNextEpisode(String cpToken, String episodeId, String seriesId, String seasonNumber, String episodeNumber,
			String tvSeasonId, Boolean seriesFlag);

	public byte[] getAssets(String dpi);
	
	public byte[] getNewAssets(String dpi);
	
	public String updateAssets();

    public String getSubscriptionPlans(String cpId, String platform, String appVersion, String deviceId, String uid,
            String token);
	
	public String refreshAssets();

	public String getAllAssets();
}
