package com.accedo.wynkstudio.dao;

import java.util.List;

import com.accedo.wynkstudio.entity.ReportAbuse;

public interface ReportAbuseDao {

	public String createReportAbuse(ReportAbuse reportAbuse);
	
	public boolean deleteReportAbuseByUserIdWithContentId(String contentId, String userId);
	
	public ReportAbuse getReportAbuseByUserIdWithContentId(String contentId, String userId);
	
	public List<ReportAbuse> listAbuse(String contentId);
	
	public List<?> summaryOfReportAbuse();
	
}
