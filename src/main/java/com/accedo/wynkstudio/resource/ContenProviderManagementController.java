package com.accedo.wynkstudio.resource;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
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
@RequestMapping({ "v0.11/", "v1/", "v0.12/", "v0.13/" })
public class ContenProviderManagementController {
	
	final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ContentProviderDelegate contentProviderDelegate;
	
	@Autowired
	CPLinkingDelegate cpLinkingDelegate;

	@Autowired
	private MessageSource messageSource;

	// /feeds/{cpToken}/programs
	@RequestMapping(value = "/feeds/{cpToken}/programs", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getProgramsById(@PathVariable("cpToken") String cpToken,
			@RequestParam(value = "category", required = false) String category,
			@RequestParam(value = "programType", required = false) String programType,
			@RequestParam(value = "order", required = false) String order,
			@RequestParam(value = "sortingKey", required = false) String sortingKey,
			@RequestParam(value = "totalPageSize", required = false) String totalPageSize,
			@RequestParam(value = "pageSize", required = false) String pageSize,
			@RequestParam(value = "pageNumber", required = false) String pageNumber, HttpServletResponse response) {
		ResponseStatus jsonStatus = ResponseStatus.SUCCESS;
		if (cpToken == null || cpToken.isEmpty()) {
			cpToken = messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_DAILYMOTION, null, "", Locale.ENGLISH);
		}
		String responseString = contentProviderDelegate.getProgramsByCategoryWithType(cpToken.toUpperCase(), category,
				programType, order, sortingKey, totalPageSize, pageSize, pageNumber);
		response.setStatus(jsonStatus.getCode());
		response.setHeader("Cache-Control", "public,max-age=900");

		return responseString;
	}

	// /feeds/{cpToken}/program/{id}
	@RequestMapping(value = "/feeds/{cpToken}/program/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getProgramById(@PathVariable("id") String id, @PathVariable("cpToken") String cpToken,
			HttpServletResponse response) {

		ResponseStatus jsonStatus = ResponseStatus.SUCCESS;
		if (cpToken == null || cpToken.isEmpty()) {
			cpToken = messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_DAILYMOTION, null, "", Locale.ENGLISH);
		}
		String responseString = contentProviderDelegate.getProgramById(cpToken.toUpperCase(), id);
		response.setStatus(jsonStatus.getCode());
		response.setHeader("Cache-Control", "public,max-age=900");

		return responseString;
	}

	// /feeds/{cpToken}/programs/banner
	@RequestMapping(value = "feeds/{cpToken}/programs/banner", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getBanner(@PathVariable("cpToken") String cpToken, HttpServletResponse response) {

		ResponseStatus jsonStatus = ResponseStatus.SUCCESS;

		if (cpToken == null || cpToken.isEmpty()) {
			cpToken = messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_DAILYMOTION, null, "", Locale.ENGLISH);
		}
		String responseString = contentProviderDelegate.getBanner(cpToken.toUpperCase());
		response.setStatus(jsonStatus.getCode());
		response.setHeader("Cache-Control", "public,max-age=900");

		return responseString;
	}

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
		String responseString = contentProviderDelegate.getSeasonBySeriesId(cpToken.toUpperCase(), id, totalPageSize, pageSize, pageNumber);
		response.setStatus(jsonStatus.getCode());
		response.setHeader("Cache-Control", "public,max-age=900");

		return responseString;
	}
	
	// /feeds/{cpToken}/episode/{id}
	@RequestMapping(value = "/feeds/{cpToken}/episode/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getSeasonById(@PathVariable("id") String id, @PathVariable("cpToken") String cpToken,
			@RequestParam(value = "seriesId", required = false, defaultValue = "") String seriesId,
			@RequestParam(value = "episodeNumber", required = false, defaultValue = "") String episodeNumber,
			@RequestParam(value = "seasonNumber", required = false, defaultValue = "") String seasonNumber,
			@RequestParam(value = "tvSeasonId", required = false, defaultValue = "") String tvSeasonId,
			@RequestParam(value = "seriesFlag", required = false) Boolean seriesFlag, HttpServletResponse response) {
		ResponseStatus jsonStatus = ResponseStatus.SUCCESS;
		if (cpToken == null || cpToken.isEmpty()) {
			cpToken = messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_DAILYMOTION, null, "", Locale.ENGLISH);
		}
		if (seriesFlag != null && seriesFlag) {
			id = URLDecoder.decode(id);
		} else {
			seriesFlag = false;
		}
		
		log.info("Next Episode call params: seriesId - " + seriesId);
		
		String responseString = contentProviderDelegate.getNextEpisode(cpToken, id, seriesId, seasonNumber,
				episodeNumber, tvSeasonId, seriesFlag);
		response.setStatus(jsonStatus.getCode());
		response.setHeader("Cache-Control", "public,max-age=900");

		return responseString;
	}

	// /feeds/{cpToken}/profiles
	@RequestMapping(value = "/feeds/{cpToken}/profiles", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getProfile(@PathVariable("cpToken") String cpToken,
			@RequestParam("releaseUrl") String releaseUrl, @RequestParam("token") String token,
			@RequestParam("uid") String uid, HttpServletResponse response) {

		if (cpToken == null || cpToken.isEmpty()) {
			cpToken = messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_EROSNOW, null, "", Locale.ENGLISH);
		}
		
		String responseString = cpLinkingDelegate.getNewProfiles(cpToken.toUpperCase(), releaseUrl, token, uid);
		JSONParser parser = new JSONParser();
		try {
			JSONObject jsonObject = (JSONObject) parser.parse(responseString);
			Long code = (Long) jsonObject.get("statusCode");
			if (code == 1402 || code == 403 || code == 400) {
				log.info("Profile Error Response: " + responseString);
				response.setStatus(404);
			} else {
				response.setStatus(code.intValue());
			}
			responseString = (String) jsonObject.get("responseBody");
			log.info("Eros Profiles Call response for uid:" + uid + ",releaseUrl:"+ releaseUrl +", rspObj:" + responseString);
		} catch (ParseException e) {
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e,
					"System encountered a Json Parse Exception");
		}

		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1.
		return responseString;
	}

	// /feeds/{cpToken}/{id}/related
	@RequestMapping(value = "/feeds/{cpToken}/{id}/related", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getRelatedProgramById(@PathVariable("id") String id,
			@PathVariable("cpToken") String cpToken, HttpServletResponse response) {

		ResponseStatus jsonStatus = ResponseStatus.SUCCESS;
		if (cpToken == null || cpToken.isEmpty()) {
			cpToken = messageSource.getMessage(CPConstants.WYNKSTUDIO_CP_TOKEN_DAILYMOTION, null, "", Locale.ENGLISH);
		}
		String responseString = contentProviderDelegate.getRelatedProgramById(cpToken.toUpperCase(), id);
		response.setStatus(jsonStatus.getCode());
		response.setHeader("Cache-Control", "public,max-age=900");

		return responseString;
	}

	/* CP Get SortingOptions list */

	@RequestMapping(value = "/{cpToken}/sortingOptions", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getSortingOptions(@PathVariable("cpToken") String cpToken, @RequestParam(value = "language", required = false) String language) {
		String responseString = "";
		responseString = contentProviderDelegate.getSortingOptions(cpToken, language);
		return responseString;
	}

	/* Subscription Plans */

	@RequestMapping(value = "/subscription/plans", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String unsubscribeUser(@RequestParam(value = "cpId", required = false) String cpId,
			@RequestParam(value = "platform", required = false, defaultValue = "") String platform,
			@RequestParam(value = "uid", required = false, defaultValue = "") String uid,
			@RequestParam(value = "token", required = false, defaultValue = "") String token,
			HttpServletResponse response) {
		String responseString = contentProviderDelegate.getSubscriptionPlans(cpId, platform, uid, token);
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}
	
	/* Get Appgrid Assets as ZIP */

	@RequestMapping(value = "/assets", method = RequestMethod.GET, produces = "application/zip")
	public @ResponseBody byte[] getAssets(@RequestParam(value = "dpi", required = false, defaultValue="") String dpi,HttpServletRequest request, HttpServletResponse response) throws IOException {
		 //setting headers
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Disposition", "attachment; filename=\"assets.zip\"");
        return contentProviderDelegate.getAssets(dpi);        
	}
	
	@RequestMapping(value = "/assets/update", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String updateAssets() {
		return contentProviderDelegate.updateAssets();
	}
	
	@RequestMapping(value = "/assets/refresh", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String refreshAssets() {
		return contentProviderDelegate.refreshAssets();
	}
	
	/* Get Assets*/
	@RequestMapping(value = "/allassets", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getAllAssets(HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache");
		return contentProviderDelegate.getAllAssets();
	}
	
	/* Get IP Address of remote location */

	@RequestMapping(value = "/ip", method = RequestMethod.GET, produces = "text/html")
	public @ResponseBody String getIp(HttpServletRequest request, HttpServletResponse response){
		return request.getRemoteAddr();
	}
}
