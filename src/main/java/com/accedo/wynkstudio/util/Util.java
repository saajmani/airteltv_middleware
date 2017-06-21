package com.accedo.wynkstudio.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXException;

import com.eclipsesource.json.ParseException;

public class Util {

	private static RestTemplate template = new RestTemplate();
	final static Logger log = LoggerFactory.getLogger("OAuth Logger");
	private static HttpHeaders headers;
	
	public static HttpHeaders setHttpHeaderAttibute() {
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.set("Accept-Encoding", "gzip, deflate");  
		return headers;
	}
	/* Make a REST GET call */
	public static String executeApiGetCall(String url) {
		String response = "";
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		template.setRequestFactory(requestFactory);
		HttpEntity<String> requestEntity = new HttpEntity<String>(setHttpHeaderAttibute());
		ResponseEntity<String> entity = template.exchange(url, HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<String>() {
				});
		if (entity.getBody() != null) {
			response = entity.getBody();
		}
		return response;
	}
	public static String executeApiGetCall(String url, HttpHeaders headers) {
		String response = "";			
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
	//	log.info("API URL:" + url + ", Start Time:" + getIST());
		ResponseEntity<String> entity = template.exchange(url, HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<String>() {
				});

		if (entity.getBody() != null) {
			response = entity.getBody();
	//		log.info("API URL:" + url + ", End Time:" + getIST());
		}
		return response;
	}
	
	/* Make a REST GET call using URI*/
	public static String executeApiGetCallUri(String url, HttpHeaders headers) {
		String response = "";
		URI uri = URI.create(url);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
	//	log.info("API URL:" + url  + ", Start Time:" + getIST());
		ResponseEntity<String> entity = template.exchange(uri, HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<String>() {
				});

		if (entity.getBody() != null) {
			response = entity.getBody();
	//		log.info("API URL:" + url + ", End Time:" + getIST());
		}
		return response;
	}
	public static String getCallWithoutRest(String myURL) {
		StringBuilder sb = new StringBuilder();
		URLConnection urlConn = null;
		InputStreamReader in = null;
		try {
			URL url = new URL(myURL);
			urlConn = url.openConnection();
			if (urlConn != null)
				urlConn.setReadTimeout(60 * 1000);
			if (urlConn != null && urlConn.getInputStream() != null) {
				in = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());
				BufferedReader bufferedReader = new BufferedReader(in);
				if (bufferedReader != null) {
					int cp;
					while ((cp = bufferedReader.read()) != -1) {
						sb.append((char) cp);
					}
					bufferedReader.close();
				}
			}
			in.close();
		} catch (Exception e) {
			throw new RuntimeException("Exception while calling URL:" + myURL, e);
		}

		return sb.toString();
	}

	/* Make a REST GET call to get Re-direct Location */
	public static String getLocationCall(String url, HttpHeaders headers) {
		String response = "";
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
	//	log.info("API URL:" + url  + ", Start Time:" + getIST());
		ResponseEntity<String> entity = template.exchange(url, HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<String>() {
				});

		if (entity.getBody() != null) {
	//		log.info("API URL:" + url + ", End Time:" + getIST());
			response = parseSmil(entity.getBody());
		}
		return response;
	}

	/* Make a REST POST call */
	public static String executeApiPostCall(String url, HttpHeaders headers, String requestBody) {
		String response = "";
		HttpEntity<String> requestEntity = new HttpEntity<String>(requestBody, headers);
	//	log.info("API URL:" + url  + ", Start Time:" + getIST());
		ResponseEntity<String> entity = template.exchange(url, HttpMethod.POST, requestEntity,
				new ParameterizedTypeReference<String>() {
				});

		if (entity.getBody() != null) {
			response = entity.getBody();
	//		log.info("API URL:" + url + ", End Time:" + getIST());
		}
		return response;
	}

	/* Util Method to convert Date to Seconds */
	public static String convertDateToSeconds(String date) {
		DateFormat formatter = null;
		Date convertedDate = null;
		formatter = new SimpleDateFormat("yyyy-MM-dd");
		long secs = 0;
		try {
			if (date != null && !date.isEmpty()) {
				convertedDate = (Date) formatter.parse(date);
				secs = (convertedDate.getTime()) / 1000;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		return Long.toString(secs);
	}

	/* Util Method to create MD5Hash */
	public static String getMD5Hash(String message) {
		String response = "";
		System.out.print("Msg:" + message);
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(message.getBytes());
			byte[] digest = md.digest();
			StringBuffer sb = new StringBuffer();
			for (byte b : digest) {
				sb.append(String.format("%02x", b & 0xff));
			}
			response = sb.toString();
			System.out.print("Hash:" + response);
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}
		return response;
	}
	
	 public static String sha1(String input) throws NoSuchAlgorithmException {
	        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
	        byte[] result = mDigest.digest(input.getBytes());
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < result.length; i++) {
	            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
	        }        
	        return sb.toString();
	    }

	/* Util Method to read from Properties File */
	public static Properties getPropValues() throws IOException {
		Properties prop = new Properties();
		String propFileName = "content-provider.properties";

		InputStream inputStream = Util.class.getClassLoader().getResourceAsStream(propFileName);

		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}
		return prop;
	}

	public static String parseSmil(String response) {
		DocumentBuilder db;
		String streamingUrl = "";
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(response));
			Document doc = db.parse(is);
			NodeList nodes = doc.getElementsByTagName("seq");
			Element seqElement = (Element) nodes.item(0);
			NodeList video = seqElement.getElementsByTagName("video");
			Element videoElement = (Element) video.item(0);
			if (videoElement != null) {
				streamingUrl = videoElement.getAttribute("src").toString() + "&guid:" + videoElement.getAttribute("guid").toString().replace("_media_", "_");
			} else {
				if(!seqElement.getAttribute("src").toString().isEmpty())
				{
					streamingUrl = seqElement.getAttribute("src").toString() + "&guid:" + seqElement.getAttribute("guid").toString().replace("_media_", "_");
				}
				else
				{
					NodeList ref = seqElement.getElementsByTagName("ref");
					Element refElement = (Element) ref.item(0);
					streamingUrl = refElement.getAttribute("src").toString() + "&guid:" + refElement.getAttribute("guid").toString().replace("_media_", "_");
				}
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return streamingUrl;
	}
	
	public static long getIST()
	{
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
		return calendar.getTimeInMillis();
	}
	
	public static void keepSearchKeyWithValues(String[] key, String[] values ) {
		for (int i = 0; i < key.length; i++) {
			WynkUtil.languageLookup.put(key[i], values[i]);
		}
	}
	
	public static String getLanguageFromSearchKey(String searchKey ) {
		String [] words = searchKey.split("\\s+");
		for (int i = 0; i < words.length; i++) {
			if(WynkUtil.languageLookup.get(words[i]) != null){
				return  WynkUtil.languageLookup.get(words[i]);
			}
		}
		return null;
	}

}
