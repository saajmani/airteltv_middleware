package com.accedo.wynkstudio.resource;

import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.accedo.wynkstudio.common.CPConstants;
import com.accedo.wynkstudio.common.ResponseStatus;
import com.accedo.wynkstudio.delegate.CPLinkingDelegate;
import com.accedo.wynkstudio.delegate.ContentProviderDelegate;
import com.accedo.wynkstudio.exception.BusinessApplicationException;

@RestController
@RequestMapping({ "v0.17/", "v0.18/" })
public class ContenProviderManagementController4 {

	final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ContentProviderDelegate contentProviderDelegate;

	@Autowired
	CPLinkingDelegate cpLinkingDelegate;

	@Autowired
	private MessageSource messageSource;

	/* Subscription Plans */

	@RequestMapping(value = "/sku", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getSkuProducts(@RequestBody String userInfoJson, HttpServletResponse response) {
		String responseString = contentProviderDelegate.getSkuProducts(userInfoJson);
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}

	@RequestMapping(value = "/feeds/{cpToken}/allepisode/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getSeasonById(@PathVariable("id") String id, @PathVariable("cpToken") String cpToken,
			@RequestParam(value = "totalPageSize", required = false) String totalPageSize,
			@RequestParam(value = "pageSize", required = false) String pageSize,
			@RequestParam(value = "pageNumber", required = false) String pageNumber, HttpServletResponse response) {

		ResponseStatus jsonStatus = ResponseStatus.SUCCESS;
		if (cpToken == null || cpToken.isEmpty()) {
			cpToken = messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_DAILYMOTION, null, "", Locale.ENGLISH);
		}
		String responseString = contentProviderDelegate.getSeasonBySeriesId(cpToken.toUpperCase(), id, totalPageSize,
				pageSize, pageNumber);
		response.setStatus(jsonStatus.getCode());
		response.setHeader("Cache-Control", "public,max-age=900");

		return responseString;
	}

	@RequestMapping(value = "/{cpToken}/sku/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getSkuEpisodes(@PathVariable("id") String id, @PathVariable("cpToken") String cpToken,
			@RequestParam(value = "totalPageSize", required = false) String totalPageSize,
			@RequestParam(value = "pageSize", required = false) String pageSize,
			@RequestParam(value = "pageNumber", required = false) String pageNumber, HttpServletResponse response) {
		String responseString = contentProviderDelegate.getSkuEpisodes(id, cpToken, totalPageSize, pageSize,
				pageNumber);
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}

}
