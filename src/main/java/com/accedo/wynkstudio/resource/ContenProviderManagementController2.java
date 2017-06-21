package com.accedo.wynkstudio.resource;

import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.accedo.wynkstudio.common.CPConstants;
import com.accedo.wynkstudio.common.ResponseStatus;
import com.accedo.wynkstudio.delegate.CPLinkingDelegate;
import com.accedo.wynkstudio.delegate.ContentProviderDelegate;

@RestController
@RequestMapping({ "v0.14/", "v0.15/" })
public class ContenProviderManagementController2 extends ContenProviderManagementController {
	
	final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ContentProviderDelegate contentProviderDelegate;
	
	@Autowired
	CPLinkingDelegate cpLinkingDelegate;

	@Autowired
	private MessageSource messageSource;

	

	// /feeds/{cpToken}/season/{id}
	@RequestMapping(value = "/feeds/{cpToken}/season/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getSeasonById(@PathVariable("id") String id, @PathVariable("cpToken") String cpToken,
			@RequestParam(value = "totalPageSize", required = false) String totalPageSize,
			@RequestParam(value = "pageSize", required = false) String pageSize,
			@RequestParam(value = "pageNumber", required = false) String pageNumber,
			HttpServletResponse response) {

		ResponseStatus jsonStatus = ResponseStatus.SUCCESS;
		if (cpToken == null || cpToken.isEmpty()) {
			cpToken = messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_DAILYMOTION, null, "", Locale.ENGLISH);
		}
		String responseString = contentProviderDelegate.getSeasonById(cpToken.toUpperCase(), id, totalPageSize, pageSize, pageNumber);
		response.setStatus(jsonStatus.getCode());
		response.setHeader("Cache-Control", "public,max-age=900");

		return responseString;
	}

}
