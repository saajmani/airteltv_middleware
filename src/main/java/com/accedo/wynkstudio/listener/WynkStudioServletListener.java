package com.accedo.wynkstudio.listener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.accedo.wynkstudio.common.CPConstants;
import com.accedo.wynkstudio.util.Util;
import com.accedo.wynkstudio.util.WynkUtil;


/**
 * @author Coladi
 *
 */
public class WynkStudioServletListener implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {

//		Initializing properties.
		Properties properties = new Properties();
		try {
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("content-provider.properties"); 
			properties.load(inputStream);
			Util.keepSearchKeyWithValues(properties.getProperty(CPConstants.WYNKSTUDIO_SEARCH_BY_LANGUAGE_KEY).split("~"), properties.getProperty(CPConstants.WYNKSTUDIO_SEARCH_BY_LANGUAGE_VALUE).split("~"));
			WynkUtil.contentProviderSingtel = properties.getProperty(CPConstants.WYNKSTUDIO_CP_TOKEN_SINGTEL);
			WynkUtil.contentProviderErosnow = properties.getProperty(CPConstants.WYNKSTUDIO_CP_TOKEN_EROSNOW);
			WynkUtil.mongodbHostPrimary = properties.getProperty(CPConstants.WYNKSTUDIO_MONGODB_HOST_PRIMARY);
			WynkUtil.mongodbHostSecondary = properties.getProperty(CPConstants.WYNKSTUDIO_MONGODB_HOST_SECONDARY);
			WynkUtil.mongodbName = properties.getProperty(CPConstants.WYNKSTUDIO_MONGODB_NAME);
			WynkUtil.mongodbPort = properties.getProperty(CPConstants.WYNKSTUDIO_MONGODB_PORT);
			
			WynkUtil.WYNKSTUDIO_JSON_FIELD_ENTRIES = properties.getProperty(CPConstants.WYNKSTUDIO_JSON_FIELD_ENTRIES);
			WynkUtil.WYNKSTUDIO_JSON_FIELD_CREDITS = properties.getProperty(CPConstants.WYNKSTUDIO_JSON_FIELD_CREDITS);
			WynkUtil.WYNKSTUDIO_JSON_FIELD_IMDBRATING = properties.getProperty(CPConstants.WYNKSTUDIO_JSON_FIELD_IMDBRATING);
			WynkUtil.WYNKSTUDIO_JSON_FIELD_CREDITTYPE = properties.getProperty(CPConstants.WYNKSTUDIO_JSON_FIELD_CREDITTYPE);
			WynkUtil.WYNKSTUDIO_JSON_FIELD_PERSONNAME = properties.getProperty(CPConstants.WYNKSTUDIO_JSON_FIELD_PERSONNAME);
			WynkUtil.WYNKSTUDIO_JSON_FIELD_CHARACTERNAME = properties.getProperty(CPConstants.WYNKSTUDIO_JSON_FIELD_CHARACTERNAME);
			WynkUtil.WYNKSTUDIO_JSON_FIELD_CREDITTYPE_ENTRIES = properties.getProperty(CPConstants.WYNKSTUDIO_JSON_FIELD_CREDITTYPE_ENTRIES);
			WynkUtil.WYNKSTUDIO_JSON_FIELD_CREDITTYPE_ALL = properties.getProperty(CPConstants.WYNKSTUDIO_JSON_FIELD_CREDITTYPE_ALL);
					
		} catch (IOException e) {
//			e.printStackTrace();
		}
	}

}
