package com.accedo.wynkstudio.resource;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ "v0.16/" })
public class ContenProviderManagementController3 extends ContenProviderManagementController2{
	@RequestMapping(value = "/assets", method = RequestMethod.GET, produces = "application/zip")
	public @ResponseBody byte[] getAssets(@RequestParam(value = "dpi", required = false, defaultValue="") String dpi,HttpServletRequest request, HttpServletResponse response) throws IOException {
		 //setting headers
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Disposition", "attachment; filename=\"assets.zip\"");
        return contentProviderDelegate.getNewAssets(dpi);        
	}
}
