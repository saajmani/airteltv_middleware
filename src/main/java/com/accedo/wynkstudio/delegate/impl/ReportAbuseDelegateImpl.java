package com.accedo.wynkstudio.delegate.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.accedo.wynkstudio.delegate.ReportAbuseDelegate;
import com.accedo.wynkstudio.entity.ReportAbuse;
import com.accedo.wynkstudio.service.ReportAbuseService;

@Component
public class ReportAbuseDelegateImpl implements ReportAbuseDelegate  {

	@Autowired
	ReportAbuseService reportAbuseService;


	@Override
	public String createReportAbuse(String userId, String userInfoJson){
		return reportAbuseService.createReportAbuse(userId, userInfoJson);
	}


	@Override
	public List<ReportAbuse> listAbuse(String contentId) {
		// TODO Auto-generated method stub
		return reportAbuseService.listAbuse(contentId);
	}


	@Override
	public List<?> summaryOfReportAbuse() {
		// TODO Auto-generated method stub
		return reportAbuseService.summaryOfReportAbuse();
	}


}
