/**
 * 
 */
package com.accedo.wynkstudio.service.impl;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import com.accedo.wynkstudio.service.ContentProviderRegisterService;

/**
 * @author Accedo
 *
 */

@Service
public class ContentProviderRegisterServiceImpl implements ContentProviderRegisterService {

	private HashMap<String, Object> contentProviderRegisterMap;

	@PostConstruct
	public void init() {
		contentProviderRegisterMap = new HashMap<>();
	}

	@Override
	public Object getContentProvider(String tokenId) {
		return contentProviderRegisterMap.get(tokenId);
	}

	@Override
	public void setContentProvider(String tokenId, Object cpObject) {
		contentProviderRegisterMap.put(tokenId, cpObject);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getContentProviderList() {
		JSONObject json = new JSONObject();
		HashMap<String, String> contentProviders = new HashMap<String, String>();
		for (String currentKey : contentProviderRegisterMap.keySet()) {
			contentProviders.put(currentKey, currentKey);
		}
		json.putAll(contentProviders);
		return json.toJSONString();
	}
}
