package com.accedo.wynkstudio.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.accedo.wynkstudio.delegate.DeepLinkDelegate;
import com.accedo.wynkstudio.helper.AppgridHelper;

@RestController
@RequestMapping({ "v0.11/", "v1/", "v0.12/", "v0.13/", "v0.14/", "v0.15/", "v0.16/", "v0.17/" , "v0.18/"})
public class DeepLinkingController {
	
	@Autowired
	DeepLinkDelegate deepLinkDelegate;
	
/* Deep-Linking */
	
	@RequestMapping(value = "/link/{cpToken}/{programType}/{id}", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String dynamicHtml(@PathVariable String cpToken, @PathVariable String programType,@PathVariable String id) {
		
		cpToken = cpToken.replace("HOOQ", "SINGTEL");
		id= id.replace("HOOQ", "SINGTEL");
		
		String appStoreUrl = AppgridHelper.appGridMetadata.get("appstore_url").asString();
		String playStoreUrl = AppgridHelper.appGridMetadata.get("playstore_url").asString();
		String webUrl = AppgridHelper.appGridMetadata.get("web_url").asString();
		String deepLinkScript =  "<script> var isMobile = { Android: function () { return navigator.userAgent.match(/Android/i); }, iOS: function () { return navigator.userAgent.match(/iPhone|iPad|iPod/i);}}; window.onload = function() {window.location = \"wynkpremiere://"+cpToken+"/"+programType+"/"+id+"\"; var url = \" \"; if(isMobile.Android()){console.log('android'); url = \"'"+ playStoreUrl +"'\";}else if(isMobile.iOS()){ console.log('ios'); url = \"'"+ appStoreUrl +"'\";}else{ console.log('computer'); url = \"'" + webUrl + "'\";} setTimeout(\"window.location = \" + url + \" ;\", 1000);}</script>";
		
	    return "<head>"+ deepLinkScript +"</head> <body></body>";
	}
	

	/* Deep-Linking */
	
	@RequestMapping(value = "/link", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String dynamicHtmlHome() {
		String appStoreUrl = AppgridHelper.appGridMetadata.get("appstore_url").asString();
		String playStoreUrl = AppgridHelper.appGridMetadata.get("playstore_url").asString();
		String webUrl = AppgridHelper.appGridMetadata.get("web_url").asString();
		String deepLinkScript =  "<script> var isMobile = { Android: function () { return navigator.userAgent.match(/Android/i); }, iOS: function () { return navigator.userAgent.match(/iPhone|iPad|iPod/i);}}; window.onload = function() {window.location = \"wynkpremiere://\"; var url = \" \"; if(isMobile.Android()){console.log('android'); url = \"'"+ playStoreUrl +"'\";}else if(isMobile.iOS()){ console.log('ios'); url = \"'"+ appStoreUrl +"'\";}else{ console.log('computer'); url = \"'" + webUrl  + "'\";} setTimeout(\"window.location = \" + url + \" ;\", 1000);}</script>";
		
	    return "<head>"+ deepLinkScript +"</head> <body></body>";
	}
	
	/* Create Short Link */
	
	 @RequestMapping(value="/masklink", method = { RequestMethod.POST },  produces = "application/json;charset=UTF-8")
	  public @ResponseBody String createShortLink(@RequestParam("url") String url) {
	    String savedLink = deepLinkDelegate.create(url);
	    return savedLink;
	  }
}
