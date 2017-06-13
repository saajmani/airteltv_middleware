package com.accedo.wynkstudio.resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.accedo.wynkstudio.common.ResponseStatus;
import com.accedo.wynkstudio.delegate.DiscoverDelegate;

@RestController
@RequestMapping({ "v0.13/", "v0.14/","v0.15/", "v0.16/", "v0.17/" , "v0.18/"})
public class DiscoverController {

	@Autowired
	DiscoverDelegate discoverDelegate;

	/* Create Filter */
	@RequestMapping(value = "/discover/filter", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public @ResponseBody String createFilter(@RequestParam("uId") String uId,
			@RequestBody String userInfoJson, 
			HttpServletResponse response) {
		ResponseStatus jsonStatus = ResponseStatus.SUCCESS;
		String responseString = discoverDelegate.createFilterById(uId, userInfoJson);
		response.setStatus(jsonStatus.getCode());
		return responseString;
	}

	/* Update Filter */
	@RequestMapping(value = "/discover/filter", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public @ResponseBody String updateFilter(@RequestParam("uId") String uId,
			@RequestBody String userInfoJson,  HttpServletResponse response) {
		ResponseStatus jsonStatus = ResponseStatus.SUCCESS;
		String responseString = discoverDelegate.updateFilterById(uId, userInfoJson);
		response.setStatus(jsonStatus.getCode());
		return responseString;
	}

	/* Get Filters */

	@RequestMapping(value = "/discover/filters", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getFilters(HttpServletResponse response) {
		ResponseStatus jsonStatus = ResponseStatus.SUCCESS;
		String responseString = discoverDelegate.getFilters();
		response.setHeader("Cache-Control", "no-cache");
		response.setStatus(jsonStatus.getCode());
		return responseString;
	}

	@RequestMapping(value = "/discover/content/filters", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getFilterContent(@RequestParam("uid") String uid, @RequestParam("filter") String filter,
			@RequestParam("programType") String programType,
			@RequestParam(value = "languages", required = false) String languages,
			@RequestParam(value = "sortingKey", required = false) String sortingKey,
			@RequestParam(value = "totalPageSize", required = false) String totalPageSize,
			@RequestParam(value = "pageSize", required = false) String pageSize,
			@RequestParam(value = "pageNumber", required = false) String pageNumber, 
			HttpServletRequest request, HttpServletResponse response) {
		String responseString = discoverDelegate.getFilterContent(uid, filter, languages, programType, sortingKey, totalPageSize, pageSize, pageNumber);
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}
	/* Get Filter list for specific user */
	@RequestMapping(value = "/discover/{uId}/filters", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getFiltersByUid(@PathVariable("uId") String uId,
			@RequestParam(value = "count", required = false) String count, @RequestParam(value = "home", required = false) String home, HttpServletResponse response) {
		ResponseStatus jsonStatus = ResponseStatus.SUCCESS;
		String responseString = discoverDelegate.getFiltersByUid(uId, count, home);
		response.setHeader("Cache-Control", "no-cache");
		response.setStatus(jsonStatus.getCode());
		return responseString;
	}

	/* Delete Filter */
	@RequestMapping(value = "/discover/delete/filter", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
	public @ResponseBody String deleteFilter(@RequestParam("uId") String uId,
			@RequestParam("filterName") String filterName, HttpServletResponse response) {
		ResponseStatus jsonStatus = ResponseStatus.SUCCESS;
		String responseString = discoverDelegate.deleteFilterByUid(uId, filterName);
		response.setStatus(jsonStatus.getCode());
		return responseString;
	}

	
}
