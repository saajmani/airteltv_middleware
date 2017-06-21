package com.accedo.wynkstudio.exception;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.accedo.wynkstudio.common.CPResponse;
import com.accedo.wynkstudio.common.ResponseStatus;
import com.accedo.wynkstudio.common.Status;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
/**
 * 
 * @author Accedo
 * <p>Global Exception Handler for Bussiness Application Exception
 * 
 */
@ControllerAdvice
public class CPExceptionControllerAdvice {
	/**
	 * Returns custom Response Object({@link CPResponse}) on occurance of Custom Exception({@link BusinessApplicationException})
	 * @param e             an Object of custom Exception class BusinessApplicationException
	 * @return FSCResponse  an Object of Custom Response 
	 */

	final Logger log = LoggerFactory.getLogger(this.getClass());

	@SuppressWarnings("rawtypes")
	@ExceptionHandler(BusinessApplicationException.class)	
	public @ResponseBody String exception(BusinessApplicationException e, HttpServletResponse  response) {

		CPResponse cpResponse = new CPResponse();
		for (ResponseStatus status : ResponseStatus.values()) {
			if (status.getCode() == e.getMsgId()) {
				Status respStatus = new Status(status);
				respStatus.setMessage(e.getMsgTokens());
				cpResponse.setStatus(respStatus);
				response.setStatus(respStatus.getCode());
				response.setContentType("application/json;charset=UTF-8");
				break;
			}
		}
		String json = "{}";

		try{
			Status respStatus = new Status();
			respStatus.setMessage(e.getMsgTokens());
			respStatus.setCode(e.getMsgId());
			cpResponse.setStatus(respStatus);
			response.setStatus(respStatus.getCode());
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			json = ow.writeValueAsString(cpResponse);
		}
		catch(java.io.IOException ex){
			log.error("Business Exception JSON Conversion Error ", ex);
		}

		return json;
	}

}


