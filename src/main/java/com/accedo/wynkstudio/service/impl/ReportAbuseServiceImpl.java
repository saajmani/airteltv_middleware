package com.accedo.wynkstudio.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.accedo.wynkstudio.dao.ReportAbuseDao;
import com.accedo.wynkstudio.entity.ReportAbuse;
import com.accedo.wynkstudio.service.ReportAbuseService;
import com.eclipsesource.json.JsonObject;

@Service

public class ReportAbuseServiceImpl implements ReportAbuseService {

	
	@Autowired		
	private ReportAbuseDao  reportAbuseDao;		
			
	final Logger log = LoggerFactory.getLogger(this.getClass());

	/*** Public Methods ***/

	@Override
	public String createReportAbuse(String userId, String userInfoJson) {
		String response = "success";
		try {
			ReportAbuse reportAbuse = createReportAbuseFromJson(userId, JsonObject.readFrom(userInfoJson));
			if(reportAbuse != null){
				response = reportAbuseDao.createReportAbuse(reportAbuse);
			}
		} catch (Exception e) {
			return "success";
		}
		return response;
	}

	private ReportAbuse createReportAbuseFromJson(String userId, JsonObject profileJsonObject) {
		ReportAbuse reportAbuse = new ReportAbuse();
		reportAbuse.setUserId(userId);
		reportAbuse.setContentId(profileJsonObject.get("contentId").asString());
		reportAbuse.setCpId(profileJsonObject.get("cpId").asString());
		reportAbuse.setMessage(profileJsonObject.get("message").asString());
		return reportAbuse;
	}

	@Override
	public List<ReportAbuse> listAbuse(String contentId) {
		// TODO Auto-generated method stub
		return reportAbuseDao.listAbuse(contentId);
	}

	@Override
	public List<?> summaryOfReportAbuse() {
		// TODO Auto-generated method stub
		return reportAbuseDao.summaryOfReportAbuse();
	}
	
}