package com.accedo.wynkstudio.test.cpmanagement;

import static org.junit.Assert.fail;

import java.util.Arrays;


//import org.apache.log4j.Logger;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.accedo.wynkstudio.exception.BusinessApplicationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


/**
 * @author Accedo Software
 * Aug 13, 2014 
 * OfferManagement.java
 */
public class CPManagementTest {
	
	RestTemplate template;
	HttpHeaders headers;
	String baseURL = "http://localhost:8080/WynkVideo/";
	

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		template = new RestTemplate();
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("cpToken", "DAILYMOTION");
		headers.set("Authorization", "Basic cm9vdDpyb290");
		//headers.add("locale", "en");
	}

	

	
	private void  printJSON(Object obj) {
		String JSONString = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			JSONString = mapper.writeValueAsString(obj);
			Logger logger = LoggerFactory.getLogger(this.getClass());
			//Logger.getLogger(this.getClass().getName()).info("JSON String = "+JSONString);
			logger.info("JSON String = "+JSONString);
		} catch (JsonProcessingException e1) {
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "System encountered a Json Parse Exception");
		}
		
		
	}
	
	/**
	 * Test method for {@link com.accedo.wynkstudio.test.cpmanagement.CPManagementTest#getCategoryList()}.
	 */
	@Test
	public final void testGetCategoryList() {
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(headers);
		ResponseEntity<Object> entity = template.exchange(
				baseURL + "category?cpToken=DAILYMOTION",
				HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<Object>() {
				});
		try {
			if (entity.getBody() != null) {
				printJSON(entity.getBody());			
			} else {
				fail("Response is null");
			}
		} catch (Exception e) {
			fail("Exception - "+e.getMessage());
		}
	}
	
	/**
	 * Test method for {@link com.accedo.wynkstudio.test.cpmanagement.CPManagementTest#getCategoryById()}.
	 */
	@Test
	public final void testGetCategoryById() {
		
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(headers);
		String id = "music";
		ResponseEntity<Object> entity = template.exchange(
				baseURL + "category/" + id +"?cpToken=DAILYMOTION",
				HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<Object>() {
				});
		try {
			if (entity.getBody() != null) {
				printJSON(entity.getBody());
			} else {
				fail("Response is null");
			}
		} catch (Exception e) {
			fail("Exception - "+e.getMessage());
		}
	}

	
	/**
	 * Test method for {@link com.accedo.wynkstudio.test.cpmanagement.CPManagementTest#getVideosList()}.
	 */
	@Test
	public final void testGetVideosList() {
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(headers);
		ResponseEntity<Object> entity = template.exchange(
				baseURL + "movie?cpToken=DAILYMOTION",
				HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<Object>() {
				});
		try {
			if (entity.getBody() != null) {
				printJSON(entity.getBody());	
			} else {
				fail("Response is null");
			}
		} catch (Exception e) {
			fail("Exception - "+e.getMessage());
		}
	}
	
	/**
	 * Test method for {@link com.accedo.wynkstudio.test.cpmanagement.CPManagementTest#getVideoById()}.
	 */
	@Test
	public final void testGetVideoById() {
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(headers);
		String id = "x2841m4";
		ResponseEntity<Object> entity = template.exchange(
				baseURL + "movie/" + id +"?cpToken=DAILYMOTION",
				HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<Object>() {
				});
		try {
			if (entity.getBody() != null) {
				printJSON(entity.getBody());
			} else {
				fail("Response is null");
			}
		} catch (Exception e) {
			fail("Exception - "+e.getMessage());
		}
	}
	
	
	/**
	 * Test method for {@link com.accedo.wynkstudio.test.cpmanagement.CPManagementTest#getVideoByCategoryId()}.
	 */
	@Test
	public final void testGetVideoByCategoryId() {
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(headers);
		String id = "shortfilms";
		ResponseEntity<Object> entity = template.exchange(
				baseURL + "category/" + id + "/movie?cpToken=DAILYMOTION",
				HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<Object>() {
				});
		try {
			if (entity.getBody() != null) {
				printJSON(entity.getBody());
			} else {
				fail("Response is null");
			}
		} catch (Exception e) {
			fail("Exception - "+e.getMessage());
		}
	}
	

	
	/**
	 * Test method for {@link com.accedo.wynkstudio.test.cpmanagement.CPManagementTest#getContentProviderList()}.
	 */
	@Test
	public final void testGetContentProviderList() {
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(headers);
		ResponseEntity<Object> entity = template.exchange(
				baseURL + "cps",
				HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<Object>() {
				});
		try {
			if (entity.getBody() != null) {
				printJSON(entity.getBody());
			} else {
				fail("Response is null");
			}
		} catch (Exception e) {
			fail("Exception - "+e.getMessage());
		}
	}
	

}
