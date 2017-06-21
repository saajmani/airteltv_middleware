package com.accedo.wynkstudio.delegate;


/**
 * 
 * @author Accedo
 * 
 *
 */
public interface UnifiedSearchDelegate {
	/**
	 * 
	 * @param genres 
	 * @param languages 
	 * @param pageNumber 
	 * @param pageSize 
	 * @param totalPageSize 
	 * @param message
	 * @return response {@link String}
	 */
	public String getSearchList(String searchKey, String cpIds, String languages, String genres, String subscription);
	
	public String getPeopleShows(String searchKey, String cpIds);
	
	public String getPeople(String searchKey);
	
	public String getSearchListByProgramType(String searchKey, String programType, String cpIds, String languages, String genres, String totalPageSize, String pageSize, String pageNumber, String subscription);

	public String getGenreList();

	public String getLanguages();
	
	public String getCps();
}
