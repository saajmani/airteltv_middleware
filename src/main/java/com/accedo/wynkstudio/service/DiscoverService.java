package com.accedo.wynkstudio.service;

public interface DiscoverService {

	public String createFilterByUid(String uId, String userInfoJson);

	public String getFilters();

	public String getFilterContent(String uid, String filter, String programType, String sortingKey, String totalPageSize, String pageSize, String pageNumber, String languages);
	
	public String getFiltersByUid(String uId, String count, String home);

	public String deleteFilterByUid(String uId, String filterName);

	public String updateFilterByUid(String id, String userInfoJson);

}
