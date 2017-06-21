package com.accedo.wynkstudio.service;

import java.util.List;

import com.accedo.wynkstudio.entity.ReportAbuse;

public interface ReportAbuseService {

	public String createReportAbuse(String userId, String userInfoJson);
	
	public List<ReportAbuse> listAbuse(String contentId);
	
	public List<?> summaryOfReportAbuse();
}
