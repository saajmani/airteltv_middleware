package com.accedo.wynkstudio.service;

public interface UnifiedSearchService {

	public String getSearchList(String searchKey, String cpIds, String languages, String genres, String subscription);

	public String getPeopleShows(String searchKey, String cpIds);

	public String getPeople(String searchKey);
	
	public String getSearchListByProgramType(String searchKey, String programType, String cpIds, String languages, String genres, String totalPageSize, String pageSize, String pageNumber, String subscription);

	public String getGenreList();

	public String getLanguages();
	
	public String getCps();

}
