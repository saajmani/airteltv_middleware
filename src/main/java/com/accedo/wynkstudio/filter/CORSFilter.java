package com.accedo.wynkstudio.filter;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Base64;

public class CORSFilter implements Filter {

	final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Controls Cross Orgin Resource Sharing. Manages the headers allowed by the
	 * application
	 */
	@SuppressWarnings("unused")
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
		response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
		response.setHeader("Content-Type", "*");
		try {
			String requestURI = ((HttpServletRequest) req).getRequestURI();
			if (requestURI.contains("/feeds/search/")) {

				String[] words = requestURI.contains("/feeds/search/movie/") ? requestURI.split("/feeds/search/movie/")
						: requestURI.contains("/feeds/search/series/") ? requestURI.split("/feeds/search/series/")
								: requestURI.contains("/feeds/search/video/") ? requestURI.split("/feeds/search/video/")
										: requestURI.contains("/feeds/search/people/")
												? requestURI.split("/feeds/search/people/")
												: requestURI.split("/feeds/search/");

				if (words.length > 1) {
					String keyWord = words[1];
					keyWord = URLDecoder.decode(keyWord, "UTF-8");
					byte[] encoded = Base64.encode(keyWord.getBytes());
					String encodedUrl = new String(encoded);
					requestURI = requestURI.replace(keyWord, encodedUrl);
					req.getRequestDispatcher(encodedUrl).forward(req, res);
				} else {
					chain.doFilter(req, res);
				}
			} else {
				chain.doFilter(req, res);
			}
		} catch (IOException e) {
			log.error("IO Exception a Filter :" + e);
		}
	}

	public void init(FilterConfig filterConfig) {
	}

	public void destroy() {
	}

}