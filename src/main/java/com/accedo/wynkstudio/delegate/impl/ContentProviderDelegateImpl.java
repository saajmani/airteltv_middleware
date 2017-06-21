package com.accedo.wynkstudio.delegate.impl;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.accedo.wynkstudio.common.CPConstants;
import com.accedo.wynkstudio.delegate.ContentProviderDelegate;
import com.accedo.wynkstudio.service.ContentProviderRegisterService;
import com.accedo.wynkstudio.service.ContentProviderService;

/**
 * 
 * @author Accedo
 * 
 *
 */

@Component
public class ContentProviderDelegateImpl implements ContentProviderDelegate {

	@Autowired
	private ContentProviderRegisterService cpRegisterService;
	
	@Autowired
	private MessageSource messageSource;

	@Override
	public String getProgramsByCategoryWithType(String cpId, String categoryId, String programType,String order, String sortingKey, String totalPageSize, String pageSize, String pageNumber) {
		ContentProviderService cpObject = (ContentProviderService) cpRegisterService.getContentProvider(messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_CONTENTPROVIDER, null, "", Locale.ENGLISH));
		return cpObject.getProgramsByCategoryWithType(cpId, categoryId, programType,order, sortingKey, totalPageSize, pageSize, pageNumber);
	}

	@Override
	public String getProgramById(String cpId, String videoId) {
		ContentProviderService cpObject = (ContentProviderService) cpRegisterService.getContentProvider(messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_CONTENTPROVIDER, null, "", Locale.ENGLISH));
		return cpObject.getProgramById(cpId, videoId);
	}

	@Override
	public String getSeasonById(String cpId, String id, String totalPageSize, String pageSize, String pageNumber) {
		ContentProviderService cpObject = (ContentProviderService) cpRegisterService.getContentProvider(messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_CONTENTPROVIDER, null, "", Locale.ENGLISH));
		return cpObject.getSeasonById(cpId, id, totalPageSize, pageSize, pageNumber);
	}

	@Override
	public String getBanner(String cpId) {
		ContentProviderService cpObject = (ContentProviderService) cpRegisterService.getContentProvider(messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_CONTENTPROVIDER, null, "", Locale.ENGLISH));
		return cpObject.getBanner(cpId);
	}
	

	@Override
	public String getSeasonBySeriesId(String cpToken, String seasonId, String totalPageSize, String pageSize, String pageNumber) {
		ContentProviderService cpObject = (ContentProviderService) cpRegisterService.getContentProvider(messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_CONTENTPROVIDER, null, "", Locale.ENGLISH));
		return cpObject.getSeasonBySeriesId(cpToken, seasonId, totalPageSize, pageSize, pageNumber);
	}

	@Override
	public String getRelatedProgramById(String cpId, String id) {
		ContentProviderService cpObject = (ContentProviderService) cpRegisterService.getContentProvider(messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_CONTENTPROVIDER, null, "", Locale.ENGLISH));
		return cpObject.getRelatedProgramById(cpId, id);
	}
	
	@Override
	public String getSortingOptions(String cpToken, String language)
	{
		ContentProviderService cpObject = (ContentProviderService) cpRegisterService.getContentProvider(messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_CONTENTPROVIDER, null, "", Locale.ENGLISH));
		return cpObject.getSortingOptions(cpToken, language);
	}
	
	@Override
	public String getNextEpisode(String cpToken, String episodeId, String seriesId, String seasonNumber,
			String episodeNumber, String tvSeasonId, Boolean seriesFlag) {
		ContentProviderService cpObject = (ContentProviderService) cpRegisterService.getContentProvider(messageSource
				.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_CONTENTPROVIDER, null, "", Locale.ENGLISH));
		return cpObject.getNextEpisode(cpToken, episodeId, seriesId, seasonNumber, episodeNumber, tvSeasonId, seriesFlag);
	}
	
	@Override
	public byte[] getAssets(String dpi)
	{
		ContentProviderService cpObject = (ContentProviderService) cpRegisterService.getContentProvider(messageSource
				.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_CONTENTPROVIDER, null, "", Locale.ENGLISH));
		return cpObject.getAssets(dpi);
	}
	
	
	public byte[] getNewAssets(String dpi)
	{
		ContentProviderService cpObject = (ContentProviderService) cpRegisterService.getContentProvider(messageSource
				.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_CONTENTPROVIDER, null, "", Locale.ENGLISH));
		return cpObject.getAssetsForNewVersion(dpi);
	}
	
	@Override
	public String updateAssets() {
		ContentProviderService cpObject = (ContentProviderService) cpRegisterService.getContentProvider(messageSource
				.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_CONTENTPROVIDER, null, "", Locale.ENGLISH));
		return cpObject.updateAssets();
	}
	
	public String refreshAssets() {
		ContentProviderService cpObject = (ContentProviderService) cpRegisterService.getContentProvider(messageSource
				.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_CONTENTPROVIDER, null, "", Locale.ENGLISH));
		return cpObject.refreshAssets();
	}

	@Override
	public String getSubscriptionPlans(String cpId, String platform, String uid, String token) {
		ContentProviderService cpObject = (ContentProviderService) cpRegisterService.getContentProvider(messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_CONTENTPROVIDER, null, "", Locale.ENGLISH));
		return cpObject.getSubscriptionPlans(cpId, platform, uid, token);
	}

	public String getAllAssets() {
		ContentProviderService cpObject = (ContentProviderService) cpRegisterService.getContentProvider(messageSource
				.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_CONTENTPROVIDER, null, "", Locale.ENGLISH));
		return cpObject.getAllAssets();
	}

}
