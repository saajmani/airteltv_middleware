package com.accedo.wynkstudio.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.accedo.wynkstudio.exception.BusinessApplicationException;
import com.accedo.wynkstudio.helper.AppgridHelper;

public class TrustedAuth {

	private Cipher signingCipher;
	
	final Logger log = LoggerFactory.getLogger("Trusted Auth Logger");

	private static final String keyStoreStrongPassword = "bre=6qebrE";
	private static final String keyStoreAlias = "idm_key";

	@SuppressWarnings("unused")
	private static final String authBaseUrl = "https://euid.theplatform.eu/idm";
	private String userInfoTemplate = "{ \"userName\":\"%s\", \"attributes\": {} }";
	private String sessionId = "UniqueSessionKey";

	@SuppressWarnings("unused")
	private static String mpxAccountId = AppgridHelper.mpxAccountId;
	private static String trustedDirectoryPid = AppgridHelper.trustedDirectoryPid;

	private RestTemplate template;
	private HttpHeaders headers;

	public TrustedAuth(String realPath) throws Exception {
		signingCipher = getSigningCipher(realPath);
		mpxAccountId = AppgridHelper.mpxAccountId;
		trustedDirectoryPid = AppgridHelper.trustedDirectoryPid;
	}

	public String getToken(String userId, String products) {
		String username = trustedDirectoryPid + '/' + userId;

		if (products != null && !products.isEmpty()) {
			userInfoTemplate = "{ \"userName\":\"%s\", \"attributes\":" + products + "}";
		} else {
			userInfoTemplate = "{ \"userName\":\"%s\", \"attributes\": {} }";
		}

		String password = buildPassword(userId);
		String response = "";

		String url = AppgridHelper.authenticatingMpxBaseUrl;
		url = url.replace("{0}", username).replace("{1}", password);
		template = new RestTemplate();
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		try {
			HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		//	log.info("MPX SignIn URL: " + url);
			ResponseEntity<String> entity = template.exchange(url, HttpMethod.GET, requestEntity,
					new ParameterizedTypeReference<String>() {
					});
			if (entity.getBody() != null) {
				response = entity.getBody();
			//	log.info("MPX SignIn Response for UID:" + userId + " is - " + response);
			}
		} catch (HttpClientErrorException e) {
			throw new BusinessApplicationException(e.getStatusCode().value(), e.getStatusText());
		}
		return response;
	}

	@SuppressWarnings("unused")
	private Cipher getSigningCipher(String realPath) throws Exception {
		KeyStore ks = KeyStore.getInstance("JKS");
		ClassLoader classLoader = getClass().getClassLoader();
		String seperator = System.getProperty("file.separator");
		File file = new File(realPath + seperator + "authfile" + seperator + "airtel_trial.jks");

		if (!file.exists())
			throw new Exception("Cannot find keystore file.");

		if (!file.canRead())
			throw new Exception("Cannot read from keystore file.");

		InputStream is = new FileInputStream(file);

		if (is == null)
			throw new Exception("Cannot load keystore file.");

		ks.load(is, keyStoreStrongPassword.toCharArray());

		Key k = ks.getKey(keyStoreAlias, keyStoreStrongPassword.toCharArray());

		if (k == null || !(k instanceof PrivateKey))
			throw new Exception("Private key not found.");

		PrivateKey key = (PrivateKey) k;

		Cipher cipher = Cipher.getInstance("RSA/ECB/NOPADDING");
		cipher.init(Cipher.ENCRYPT_MODE, key);

		byte[] base64Key = Base64.encode(key.getEncoded());

		return cipher;
	}

	private String buildPassword(String userId) {
		byte[] sessionKey = sessionId.getBytes();

		byte[] encryptedSessionKey = encrypt("Session Key", signingCipher, sessionKey);

		String userInfoJsonString = String.format(userInfoTemplate, userId);
		byte[] userInfoJson = userInfoJsonString.getBytes();

		byte[] encryptedUserInfoJson = encrypt("UserInfo", getUserInfoCipher(sessionKey), userInfoJson);

		return new String(encryptedSessionKey) + '|' + new String(encryptedUserInfoJson);
	}

	private byte[] encrypt(String loggingTitle, Cipher cipher, byte[] data) {
		try {
			byte[] base64Key = Base64.encode(cipher.doFinal(data));

			return base64Key;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Cipher getUserInfoCipher(byte[] sessionKey) {
		Cipher userEncryptCipher;
		try {
			SecretKeySpec secretKeySpec = new SecretKeySpec(sessionKey, "RC4");
			userEncryptCipher = Cipher.getInstance("RC4");
			userEncryptCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return userEncryptCipher;
	}

	@SuppressWarnings("unused")
	private String printBytes(byte[] bytes) {
		return new String(bytes);

	}
}
