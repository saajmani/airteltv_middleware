package com.accedo.wynkstudio.delegate.impl;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.accedo.wynkstudio.common.CPConstants;
import com.accedo.wynkstudio.delegate.CPLinkingDelegate;
import com.accedo.wynkstudio.delegate.ContentProviderDelegate;
import com.accedo.wynkstudio.service.ContentProviderRegisterService;
import com.accedo.wynkstudio.service.ContentProviderService;
import com.accedo.wynkstudio.service.CpLinkingService;

/**
 * 
 * @author Accedo
 * 
 *
 */

@Component
public class CPLinkingDelegateImpl implements CPLinkingDelegate {

	@Autowired
	private ContentProviderRegisterService cpRegisterService;
	
	@Autowired
	private CpLinkingService cpLinkingService;
	
	@Autowired
	private MessageSource messageSource;

	

	@Override
	public String getNewProfiles(String cpToken, String contentId, String token, String uid) {
	//	ContentProviderService cpObject = () cpRegisterService.getContentProvider(messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_CONTENTPROVIDER, null, "", Locale.ENGLISH));
		return cpLinkingService.getNewProfiles(cpToken, contentId, token, uid);
	}
	
	
}
