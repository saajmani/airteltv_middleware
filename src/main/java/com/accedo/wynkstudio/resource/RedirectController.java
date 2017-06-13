package com.accedo.wynkstudio.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.accedo.wynkstudio.service.DeepLinkService;

@Controller
@RequestMapping({"/", "v0.11/", "v1/", "v0.12/", "v0.13/", "v0.14/", "v0.15/", "v0.16/", "v0.17/" , "v0.18/"})
public class RedirectController {
  private DeepLinkService linkService;

  @Autowired
  public RedirectController(DeepLinkService linkService) {
    this.linkService = linkService;
  }
  
  @RequestMapping(value = "/s/{hashCode}", method = {RequestMethod.GET, RequestMethod.HEAD})
  public String redirectContext(@PathVariable("hashCode") String hashCode) {
		 return "redirect:" + linkService.getUrlByHash(hashCode);
  }
  
  @RequestMapping(value = "/{offerId}/image", method = {RequestMethod.GET, RequestMethod.HEAD})
  public String redirectToOfferImage(@PathVariable("offerId") String offerId) {
		 return "redirect:" + linkService.getOfferImage(offerId);
  }
  
}