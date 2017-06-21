package com.accedo.wynkstudio.resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

	@RequestMapping(value = "/health", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getHealth(HttpServletRequest request, HttpServletResponse response) {
		response.setStatus(HttpStatus.OK.value());
		String responseString = "200 OK: Service Up";
		return responseString;
	}

}
