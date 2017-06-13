package com.accedo.wynkstudio.resource;

/**
 * @author      Accedo Software Private Limited 
 * @version     1.0                 
 * @since       2014-07-01   
 * */
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.accedo.wynkstudio.common.ResponseStatus;
import com.accedo.wynkstudio.delegate.UnifiedSearchDelegate;

@RestController
@RequestMapping({ "v0.11/", "v1/", "v0.12/", "v0.13/", "v0.14/", "v0.15/", "v0.16/", "v0.17/" , "v0.18/"})
public class UnifiedSearchManagementController {

	@Autowired
	UnifiedSearchDelegate unifiedSearchDelegate;

	@Autowired
	private MessageSource messageSource;

	// /feeds/search/{query}
	@RequestMapping(value = "/feeds/search/{searchKey}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getSearchList(HttpServletResponse response,
			@PathVariable("searchKey") String searchKey,
			@RequestParam(value = "cpIds", required = false, defaultValue = "") String cpIds,
			@RequestParam(value = "languages", required = false, defaultValue = "") String languages,
			@RequestParam(value = "genres", required = false, defaultValue = "") String genres,
			@RequestParam(value = "subscription", required = false, defaultValue = "") String subscription){
		ResponseStatus jsonStatus = ResponseStatus.SUCCESS;
	searchKey = new String( Base64.decode(searchKey.getBytes()));
		searchKey = searchKey.trim().replace(":", "").replace("(", "").replace("&","38");
		String responseString = unifiedSearchDelegate.getSearchList(searchKey, cpIds, languages, genres, subscription);
		response.setStatus(jsonStatus.getCode());
		response.setHeader("Cache-Control", "public,max-age=900"); // HTTP 1.1.
		return responseString;
	}

	// /feeds/people/{query}
	@RequestMapping(value = "/feeds/people/{searchKey}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getPeopleRelated(HttpServletResponse response,
			@PathVariable("searchKey") String searchKey,
			@RequestParam(value = "cpIds", required = false, defaultValue = "") String cpIds) {
		ResponseStatus jsonStatus = ResponseStatus.SUCCESS;
//		searchKey = new String( Base64.decode(searchKey.getBytes()));
		searchKey = searchKey.trim().replace(":", "").replace("(", "").replace("&","38");
		String responseString = unifiedSearchDelegate.getPeopleShows(searchKey, cpIds);
		response.setStatus(jsonStatus.getCode());
		response.setHeader("Cache-Control", "public,max-age=900"); // HTTP 1.1.
		return responseString;
	}

	// /people/{query}
	@RequestMapping(value = "/people/{searchKey}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getPeople(HttpServletResponse response, @PathVariable("searchKey") String searchKey) {
		ResponseStatus jsonStatus = ResponseStatus.SUCCESS;
		searchKey = searchKey.trim().replace(":", "").replace("(", "").replace("&","38");;
		String responseString = unifiedSearchDelegate.getPeople(searchKey);
		response.setStatus(jsonStatus.getCode());
		response.setHeader("Cache-Control", "public,max-age=900"); // HTTP 1.1.
		return responseString;
	}
	
	@RequestMapping(value = "/feeds/search/{programType}/{searchKey}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getSearchListByProgramType(HttpServletResponse response,
			@PathVariable("searchKey") String searchKey,@PathVariable("programType") String programType,
			@RequestParam(value = "totalPageSize", required = false) String totalPageSize,
			@RequestParam(value = "pageSize", required = false) String pageSize,
			@RequestParam(value = "pageNumber", required = false) String pageNumber, 
			@RequestParam(value = "cpIds", required = false, defaultValue = "") String cpIds,
			@RequestParam(value = "languages", required = false, defaultValue = "") String languages,
			@RequestParam(value = "genres", required = false, defaultValue = "") String genres,
			@RequestParam(value = "subscription", required = false, defaultValue = "") String subscription) {
		ResponseStatus jsonStatus = ResponseStatus.SUCCESS;
		searchKey = searchKey != null && !searchKey.isEmpty() ? new String(Base64.decode(searchKey.getBytes())) : " ";
		searchKey = searchKey.trim().replace(":", "").replace("(", "").replace("&","38");;
		String responseString = unifiedSearchDelegate.getSearchListByProgramType(searchKey, programType, cpIds, languages, genres, totalPageSize, pageSize, pageNumber, subscription);
		response.setStatus(jsonStatus.getCode());
		response.setHeader("Cache-Control", "public,max-age=900"); // HTTP 1.1.
		return responseString;
	}
	
	@RequestMapping(value = "/genres", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getGenreList(HttpServletResponse response) {
		ResponseStatus jsonStatus = ResponseStatus.SUCCESS;
		String responseString = unifiedSearchDelegate.getGenreList();
		response.setStatus(jsonStatus.getCode());
		response.setHeader("Cache-Control", "public,max-age=900"); // HTTP 1.1.
		return responseString;
	}
	
	@RequestMapping(value = "/languages", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getLanguages(HttpServletResponse response) {
		ResponseStatus jsonStatus = ResponseStatus.SUCCESS;
		String responseString = unifiedSearchDelegate.getLanguages();
		response.setStatus(jsonStatus.getCode());
		response.setHeader("Cache-Control", "public,max-age=900"); // HTTP 1.1.
		return responseString;
	}
	
	@RequestMapping(value = "/cps", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getCps(HttpServletResponse response) {
		ResponseStatus jsonStatus = ResponseStatus.SUCCESS;
		String responseString = unifiedSearchDelegate.getCps();
		response.setStatus(jsonStatus.getCode());
		response.setHeader("Cache-Control", "public,max-age=900"); // HTTP 1.1.
		return responseString;
	}

}
