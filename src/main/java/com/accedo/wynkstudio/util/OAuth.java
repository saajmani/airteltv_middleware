package com.accedo.wynkstudio.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.accedo.wynkstudio.exception.BusinessApplicationException;
import com.eclipsesource.json.JsonObject;


public class OAuth {

	private static final String SIGNATURE_METHOD = "HMAC-SHA1";
	private static final String CONSUMER_KEY = "1a5bd8d151799fec5c7622bacef0b4b505459f23f";
	private static final String CONSUMER_SECRET = "9c10ca4b76ab49f44889065d6c0203a1";
	

	public OAuth() {
	}

	
	@SuppressWarnings("unchecked")
	public static String OAuthRequestToken(String httpMethod, String authToken, String tokenSecret, String baseurl, String userJson, String type) {
		byte[] requestBody = null;
		HttpURLConnection request = null;
		BufferedReader in = null;
		String body = "";
		String response = null;
		String oauth_token_value = "";
		String email = "";
		String partnerid = "";
		if(userJson != null)
		{
			if(JsonObject.readFrom(userJson).get("unique_code") != null && !JsonObject.readFrom(userJson).get("unique_code").isNull())
			{
				JsonObject.readFrom(userJson).get("unique_code").asLong();
			}
		}
		JSONObject reponseJson = new JSONObject();
		JsonObject userJsonObject = null;

		try {
			URL url = new URL(String.format(baseurl));
			String nonce = getNonce();
			String timestamp = getTimestamp();
			
			if(userJson != null && !userJson.isEmpty())
			{
				userJsonObject = JsonObject.readFrom(userJson);
				email = userJsonObject.get("email").asString();
				partnerid = userJsonObject.get("partnerid").asString();
			}

			// Set the request body if making a POST or PUT request
			if (("POST".equals(httpMethod) || "PUT".equals(httpMethod))	&& type.equalsIgnoreCase("login")) {
				body = "&id=" + partnerid + "&partner=WYNK";
				requestBody = body.getBytes("UTF-8");
			} else if (userJson != null && ("POST".equals(httpMethod) || "PUT".equals(httpMethod))
					&& type.equalsIgnoreCase("register")) {
				body = "email=" + email + "&partnerid=" + partnerid + "&country=IN&partner=WYNK";
				requestBody = body.getBytes("UTF-8");
			} else if (type.equalsIgnoreCase("purchase") || type.equalsIgnoreCase("cancel"))
			{
				body = "partner=WYNK&country_code=IN&partnerid="+ partnerid + "&product=1000015&plan=" + userJsonObject.get("plan").asString() + "&payment_id=1000002";
				requestBody = body.getBytes("UTF-8");
			}

			// Create the OAuth parameter name/value pair
			Map<String, String> oauthParams = new LinkedHashMap<String, String>();
			oauthParams.put("oauth_consumer_key", CONSUMER_KEY);
			oauthParams.put("oauth_nonce", nonce);
			oauthParams.put("oauth_signature_method", SIGNATURE_METHOD);
			oauthParams.put("oauth_timestamp", timestamp);
			if (authToken != null)
				oauth_token_value = authToken;

			oauthParams.put("oauth_token", oauth_token_value);
			oauthParams.put("oauth_version", "1.0");

			if (("POST".equals(httpMethod) || "PUT".equals(httpMethod)) && type.equalsIgnoreCase("login")) {
			//	oauthParams.put("email", email);
				oauthParams.put("id", partnerid);
				oauthParams.put("partner", "WYNK");
			}
			else if (("POST".equals(httpMethod) || "PUT".equals(httpMethod)) && type.equalsIgnoreCase("register"))
			{
				oauthParams.put("email", email);
				oauthParams.put("partnerid", partnerid);
				oauthParams.put("country", "IN");
				oauthParams.put("partner", "WYNK");
			}
			else if (type.equalsIgnoreCase("purchase") || type.equalsIgnoreCase("cancel"))
			{
				oauthParams.put("partnerid", partnerid);
				oauthParams.put("partner", "WYNK");
				oauthParams.put("product", "1000015");
				oauthParams.put("country_code", "IN");
				oauthParams.put("plan", userJsonObject.get("plan").asString());
				oauthParams.put("payment_id", "1000002");
			}

			// Get the OAuth 1.0 Signature
			String signature = generateSignature(httpMethod, url, oauthParams, requestBody, CONSUMER_SECRET,
					tokenSecret);
			System.out.println(String.format("OAuth 1.0 Signature = %s", signature));
			oauthParams.put("oauth_signature", signature);
			
			System.setProperty("jsse.enableSNIExtension", "false");

			// Generate a string of comma delimited:
			// keyName="URL-encoded(value)" pairs
			int i = 0;
			StringBuilder sb = new StringBuilder();
			Object[] keyNames = oauthParams.keySet().toArray();
			for (Object keyName : keyNames) {
				String value = oauthParams.get((String) keyName);
				sb.append(keyName).append('=').append(URLEncoder.encode(value, "UTF-8"));
				i++;

				if (keyNames.length > i) {
					sb.append(',');
				}
			}

			// Build the Authorization request header
			String xauth = String.format("OAuth %s", sb.toString());
			System.out.println(String.format("Authorization request header = %s", xauth));

			// Setup the Request
			request = (HttpURLConnection) url.openConnection();

			request.setRequestMethod(httpMethod);
			request.setRequestProperty("Authorization", xauth);
			request.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			request.setRequestProperty("Accept", "" + "application/json");
			request.setRequestProperty(
					"User-Agent",
					""
							+ "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36");

			// Set the request body if making a POST or PUT request
			if ("POST".equals(httpMethod) || "PUT".equals(httpMethod)) {
				byte[] byteArray = body.getBytes("UTF-8");
				request.setRequestProperty("Content-Length", "" + byteArray.length);
				request.setDoOutput(true);

				OutputStream postStream = request.getOutputStream();
				postStream.write(byteArray, 0, byteArray.length);
				postStream.close();
			}

			// Send Request & Get Response
			InputStreamReader reader = new InputStreamReader(request.getInputStream());
			in = new BufferedReader(reader);

			// Get the response stream
			reponseJson.put("statusCode", 200);
			reponseJson.put("responseBody", in.readLine());
			response = reponseJson.toJSONString();
			
			final Logger log2 = LoggerFactory.getLogger("OAuth Logger");
			log2.info("Eros API Response for uid:" + userJson +",for API:" + baseurl + ":" + response);

			if (in != null)
				in.close();
		} catch (IOException e) {
			final Logger log1 = LoggerFactory.getLogger("OAuth Logger");
			String responseBody = "";
			try
			{
			InputStreamReader error = new InputStreamReader(request.getErrorStream());
			in = new BufferedReader(error);
			responseBody = in.readLine();
			}
			catch(Exception e1)
			{
				log1.error("New Error:"+ e1);
			}
			
			try {
				int responseCode = request.getResponseCode();
				
				if(responseBody != null && responseBody.contains("1402"))
				{
					reponseJson.put("statusCode", 1402);
				}
				else
				{
					reponseJson.put("statusCode", responseCode);
				}
				reponseJson.put("responseBody", responseBody);
				
				log1.error("ErosNow API Error Response: ErrorCode =" + request.getResponseCode() +", body=" + responseBody + ", userInfo=" + userJson + ", api=" + baseurl);	
				response = reponseJson.toJSONString();
			} catch (IOException e1) {
				throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e1, e1.getMessage());
			}
		} finally {
			if (request != null)
				request.disconnect();
		}

		return response;
	}


	private static String normalizeParams(String httpMethod, URL url, Map<String, String> oauthParams,
			byte[] requestBody) throws UnsupportedEncodingException {
		// Use a new LinkedHashMap for the OAuth signature creation
		Map<String, String> kvpParams = new LinkedHashMap<String, String>();
		kvpParams.putAll(oauthParams);

		// Place any query string parameters into a key value pair using equals
		// ("=") to mark
		// the key/value relationship and join each parameter with an ampersand
		// ("&")
		if (url.getQuery() != null) {
			for (String keyValue : url.getQuery().split("&")) {
				String[] p = keyValue.split("=");
				kvpParams.put(URLEncoder.encode(p[0], "UTF-8"), URLEncoder.encode(p[1], "UTF-8"));
			}
		}

		// Sort the parameters in lexicographical order, 1st by Key then by
		// Value; separate with ("=")
		TreeMap<String, String> sortedParams = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		sortedParams.putAll(kvpParams);

		// Remove unwanted characters and replace the comma delimiter with an
		// ampersand
		String stringParams = sortedParams.toString().replaceAll("[{} ]", "");
		stringParams = stringParams.replace(",", "&");

		// URL-encode the equals ("%3D") and ampersand ("%26")
		String encodedParams = URLEncoder.encode(stringParams, "UTF-8");

		return encodedParams;
	}


	private static String generateSignature(String httpMethod, URL url, Map<String, String> oauthParams,
			byte[] requestBody, String secret, String tokenSecret) throws UnsupportedEncodingException {
		String result = null;
		try {
			// Ensure the HTTP Method is upper-cased
			httpMethod = httpMethod.toUpperCase();

			// Construct the URL-encoded OAuth parameter portion of the
			// signature base string
			String encodedParams = normalizeParams(httpMethod, url, oauthParams, requestBody);

			// URL-encode the relative URL
			String encodedUri = URLEncoder.encode(url.getProtocol() + "://" + url.getHost() + url.getPath(), "UTF-8");
			System.out.println(String.format("OAuth encodedUri1 = %s", encodedUri));
			// Build the signature base string to be signed with the Consumer
			// Secret
			String baseString = String.format("%s&%s&%s", httpMethod, encodedUri, encodedParams);
			System.out.println(String.format("OAuth baseString = %s", baseString));
			baseString = baseString.replace("%40", "%2540");
			System.out.println(String.format("OAuth baseString Modified String = %s", baseString));
			StringBuffer sbuffer = new StringBuffer(baseString);
			System.out.println(String.format("OAuth baseString buffer = %s", sbuffer));

			System.out.println("");
			System.out.println(String.format("OAuth secret = %s", secret));
			System.out.println("");
			if (!tokenSecret.isEmpty()) {
				secret = secret + '&' + tokenSecret;
			} else {
				secret = secret + '&';
			}

			result = Signature.calculateRFC2104HMAC(sbuffer.toString(), secret);
		} catch (Exception e) {
			throw new BusinessApplicationException(e.hashCode(), e.getMessage());
		}
		return result;
	}


	private static String getNonce() {
		return RandomStringUtils.randomAlphanumeric(32);
	}

	
	private static String getTimestamp() {
		return Long.toString((System.currentTimeMillis() / 1000));
	}

}
