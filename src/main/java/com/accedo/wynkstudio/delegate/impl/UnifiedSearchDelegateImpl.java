package com.accedo.wynkstudio.delegate.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.accedo.wynkstudio.delegate.UnifiedSearchDelegate;
import com.accedo.wynkstudio.service.UnifiedSearchService;

/**
 * 
 * @author Accedo
 * 
 *
 */

@Component
public class UnifiedSearchDelegateImpl implements UnifiedSearchDelegate {

	@Autowired
	private UnifiedSearchService unifiedSearchService;

	@Override
	public String getSearchList(String searchKey, String cpIds, String languages, String genres, String subscription) {
		return unifiedSearchService.getSearchList(searchKey, cpIds, languages, genres, subscription);
	}
	
	@Override
	public String getPeopleShows(String searchKey, String cpIds) {
		return unifiedSearchService.getPeopleShows(searchKey, cpIds);
	}
	
	@Override
	public String getPeople(String searchKey) {
		return unifiedSearchService.getPeople(searchKey);
	}

	@Override
	public String getSearchListByProgramType(String searchKey, String programType, String cpIds, String languages, String genres, String totalPageSize, String pageSize,
			String pageNumber, String subscription) {
		return unifiedSearchService.getSearchListByProgramType(searchKey, programType, cpIds, languages, genres, totalPageSize, pageSize, pageNumber, subscription);
	}

	@Override
	public String getGenreList() {
		return unifiedSearchService.getGenreList();
	}

	@Override
	public String getLanguages() {
		return unifiedSearchService.getLanguages();
	}
	
	@Override
	public String getCps() {
		return unifiedSearchService.getCps();
	}
}
