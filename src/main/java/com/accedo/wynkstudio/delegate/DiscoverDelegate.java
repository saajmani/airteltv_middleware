package com.accedo.wynkstudio.delegate;

/**
 * 
 * @author Accedo
 * 
 *
 */
public interface DiscoverDelegate {

	public String createFilterById(String uId, String userInfoJson);

	public String getFilters();

	public String getFilterContent(String uid, String filter, String languages, String programType, String sortingKey, String totalPageSize, String pageSize, String pageNumber);
	
	public String getFiltersByUid(String uId, String count, String home);

	public String deleteFilterByUid(String uId, String filterName);

	public String updateFilterById(String uId, String userJsonIn);

}
	
