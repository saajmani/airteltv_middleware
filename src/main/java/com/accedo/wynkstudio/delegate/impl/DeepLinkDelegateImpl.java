package com.accedo.wynkstudio.delegate.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.accedo.wynkstudio.delegate.DeepLinkDelegate;
import com.accedo.wynkstudio.service.DeepLinkService;

@Component
public class DeepLinkDelegateImpl implements DeepLinkDelegate {
	
	@Autowired
	private DeepLinkService deepLinkService;
	
	@Autowired
	private MessageSource messageSource;

	@Override
	public String create(String url) {
		return deepLinkService.create(url);
	}
	
}
