package com.accedo.wynkstudio.delegate;

import java.util.List;

import com.accedo.wynkstudio.entity.ReportAbuse;

/**
 * 
 * @author Accedo
 * 
 *
 */
public interface ReportAbuseDelegate {
	public String createReportAbuse(String userId, String userInfoJson);
	public List<ReportAbuse> listAbuse(String contentId);
	public List<?> summaryOfReportAbuse();
}
