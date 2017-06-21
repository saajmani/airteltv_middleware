package com.accedo.wynkstudio.test.usermanagement;

import static org.junit.Assert.fail;

import java.util.Arrays;


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
import com.accedo.wynkstudio.vo.AccountVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * @author Accedo Software
 * Aug 13, 2014 
 * OfferManagement.java
 */
public class UserManagementTest {
	
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
		//headers.set("Authorization", "Basic cm9vdDpyb290");
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
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e1.getMessage());
		}
		
		
	}
	
	/**
	 * Test method for {@link com.accedo.wynkstudio.test.usermanagement.UserManagementTest#getUserAccount()}.
	 */
	@Test
	public final void testGetUserAccount() {
//		userProfileVO.setMsisdn("9995224351");
//		userProfileVO.setOtp("+919995224351");
		headers.add("X-MSISDN", "9995224351");
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		ResponseEntity<Object> entity = template.exchange(
				baseURL + "account",
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
	 * Test method for {@link com.accedo.wynkstudio.test.usermanagement.UserManagementTest#createUserAccount()}.
	 */
	@Test
	public final void testCreateUserAccount() {
		AccountVO userProfileVO  = new AccountVO();
//		userProfileVO.setMsisdn("9995224351");
//		userProfileVO.setOtp("+9995224351");
		headers.add("X-MSISDN", "9995224351");
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(userProfileVO, headers);
		ResponseEntity<Object> entity = template.exchange(
				baseURL + "account",
				HttpMethod.POST, requestEntity,
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
	 * Test method for {@link com.accedo.wynkstudio.test.usermanagement.UserManagementTest#GetOneTimePassword()}.
	 */
	@Test
	public final void testGetOneTimePassword() {
		AccountVO userProfileVO  = new AccountVO();
		userProfileVO.setMsisdn("9995224351");
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(userProfileVO, headers);
		ResponseEntity<Object> entity = template.exchange(
				baseURL + "account/otp",
				HttpMethod.POST, requestEntity,
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
	 * Test method for {@link com.accedo.wynkstudio.test.usermanagement.UserManagementTest#getUserProfile()}.
	 */
	@Test
	public final void testGetUserProfile() {
		headers.add("X-MSISDN", "9995224351");
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		ResponseEntity<Object> entity = template.exchange(
				baseURL + "account/profile?uid=svOB6D1gbM66YNAwO6PSklpPlzI1&token=61Acjakk",
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
	 * Test method for {@link com.accedo.wynkstudio.test.usermanagement.UserManagementTest#getUserProfile()}.
	 */
	@Test
	public final void testUpdateUserProfile() {
		
		String requiestBody = "{\"email\" : \"test@test.in\",\"gender\":\"m\",\"dob\":{\"month\":2,\"year\":1989,\"day\":2},\"name\":\"Test\",\"songQuality\":\"h\",\"contentLang\":[\"en\",\"hi\",\"pa\"],\"lang\":\"en\"}";
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(requiestBody, headers);
		ResponseEntity<Object> entity = template.exchange(
				baseURL + "account/profile?uid=svOB6D1gbM66YNAwO6PSklpPlzI1&token=61Acjakk",
				HttpMethod.POST, requestEntity,
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
