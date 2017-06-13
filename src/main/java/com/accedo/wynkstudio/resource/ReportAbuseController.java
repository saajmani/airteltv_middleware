package com.accedo.wynkstudio.resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.accedo.wynkstudio.delegate.ReportAbuseDelegate;

@Controller
@RequestMapping({ "v0.11/", "v1/", "v0.12/", "v0.13/", "v0.14/", "v0.15/", "v0.16/", "v0.17/" , "v0.18/"})
public class ReportAbuseController {
	
	@Autowired
	ReportAbuseDelegate reportAbuseDelegate;
	
	/* Subscription Check */

	@RequestMapping(value = "/report/content", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getEntitlementStatus(@RequestParam("uid") String uid, @RequestBody String userInfoJson,
			HttpServletRequest request, HttpServletResponse response) {
		String responseString = reportAbuseDelegate.createReportAbuse(uid, userInfoJson);
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}
	
	
	@RequestMapping(value = "/report/list")
	   public String listAbuse(ModelMap model, @RequestParam(value = "contentId", required = false, defaultValue = "") String contentId) {
	      model.addAttribute("message", "Report Abuse by Content : " + contentId);
	      model.addAttribute("reportabuses", reportAbuseDelegate.listAbuse(contentId));
	      return "reportabuse";
	   }
	
	@RequestMapping(value = "/report/summary")
	   public String summaryReport(ModelMap model) {
	      model.addAttribute("message", "Summary Of Report Abuse");
	      model.addAttribute("summaries", reportAbuseDelegate.summaryOfReportAbuse());
	      return "summary";
	   }

}
