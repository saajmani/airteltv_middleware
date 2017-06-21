package com.accedo.wynkstudio.filter;

import java.io.File;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.accedo.wynkstudio.common.CPConstants;
import com.accedo.wynkstudio.util.HeaderMapRequestWrapper;
import com.accedo.wynkstudio.util.RSAKeyGenerationUtil;
import com.accedo.wynkstudio.util.StringUtil;
import com.accedo.wynkstudio.util.TokenGenerationUtil;

import net.iharder.Base64;

/**
 * @author Accedo Software Private Limited
 * @version 1.0
 * @since 2014-07-01
 * 
 *        <p>
 *        Custom Authentication Filter for authenticating applications
 * */
@Component
public class SCAuthenticationFilter implements Filter {

	private String authenticationToken;

	public String getAuthenticationToken() {
		return authenticationToken;
	}

	public void setAuthenticationToken(String authenticationToken) {
		this.authenticationToken = authenticationToken;
	}

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		/**
		 * Retrieve msisdn, Application Id, Locale, Date,Application Token and
		 * User Token
		 * 
		 * @param AppAuth
		 *            Application token
		 * @param Date
		 *            Time Stamp
		 * @return
		 */

		File privateKeyFile = null;
		String date = null;
		String applicationToken = null;
		if (StringUtil.notEmpty(req
				.getHeader(CPConstants.TIME_STAMP_HEADER_NAME))
				&& StringUtil.notEmpty(req
						.getHeader(CPConstants.APPLICATION_TOKEN_HEADER_NAME))) {
			try {
				ClassLoader classLoader = getClass().getClassLoader();
			 privateKeyFile = new File(classLoader.getResource(CPConstants.RSA_PPRIVATE_KEY_LOCATION).getFile());

				date = req.getHeader(CPConstants.TIME_STAMP_HEADER_NAME);
				applicationToken = new String(
						RSAKeyGenerationUtil.decryptNew(
								Base64.decode(req
										.getHeader(CPConstants.APPLICATION_TOKEN_HEADER_NAME)),
								privateKeyFile));
/*

				/**
				 * Application Authentication
				 */

				if (authenticateApplication(applicationToken, date, privateKeyFile)) {

					HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(
							req);
					/**
					 * Setting user id and application id for the controllers
					 * for validation and the Locale for message display
					 * according to the locale
					 * 
					 * @param msisdn
					 * @param appId
					 * @param locale
					 * @return
					 */
					// requestWrapper.addHeader("locale", locale);
					chain.doFilter(requestWrapper, res);
				} else {
					// Authentication Failed
					res.setStatus(401);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// Syntactically incorrect
			res.setStatus(400);
		}

	}

	private boolean authenticateApplication(String encodedApplicationToken,
			String date, File privateKeyFile) {
		boolean retVal = false;
		String applicationToken = TokenGenerationUtil
				.decodeApplicationToken(encodedApplicationToken);
		String appendedTimeStamp = TokenGenerationUtil
				.decodeTimeStamp(encodedApplicationToken);
			// Set Application Id.
				if (applicationToken.equals("2b2c983cc4f4de111c53e962c8ea3c4f")
						&& appendedTimeStamp.equals(date)) {
					// Authentication is successful if the send application
					// token
					// and the retrieved application token matches and the
					// appended
					// time stamp matched the date header
					retVal = true;
		}
		return retVal;
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
//		@SuppressWarnings("resource")
		/*ApplicationContext context = new ClassPathXmlApplicationContext(
				"application-config.xml");*/
	}

	/*
	 * @param
	 */

}
