package com.accedo.wynkstudio.resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.accedo.wynkstudio.common.ResponseStatus;
import com.accedo.wynkstudio.delegate.DiscoverDelegate;
import com.accedo.wynkstudio.delegate.PaymentTransactionDelegate;

@RestController
@RequestMapping({ "v0.17/" , "v0.18/"})
public class PaymentController {

	@Autowired
	PaymentTransactionDelegate paymentTransactionDelegate;

	@RequestMapping(value = "/payment", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public @ResponseBody String createPayment(@RequestBody String userInfoJson,
			HttpServletRequest request, HttpServletResponse response) {
		String responseString = paymentTransactionDelegate.createPayment(userInfoJson);
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}
	@RequestMapping(value = "/payment", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public @ResponseBody String updatePayment(@RequestBody String userInfoJson,
			HttpServletRequest request, HttpServletResponse response) {
		String responseString = paymentTransactionDelegate.updatePayment(userInfoJson);
		response.setHeader("Cache-Control", "no-cache");
		return responseString;
	}
}
