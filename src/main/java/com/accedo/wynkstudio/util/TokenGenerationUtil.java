package com.accedo.wynkstudio.util;
/**
 * @author      Accedo Software Private Limited 
 * @version     1.0                 
 * @since       2014-07-01   
 * */
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.accedo.wynkstudio.common.CPConstants;

import net.iharder.Base64;

public class TokenGenerationUtil {

	public static String generateToken(){		
		
		return  new BigInteger(50, new SecureRandom()).toString(64);
	}
	
	public static String basicHttpAuthenticationHeaderFormat(String username , String password){	
		StringBuilder basicHttpAuthenticationHeader = new StringBuilder();
		String token = username+":"+password;
		String base64EncodedString = new String(Base64.encodeBytes(token.getBytes()));
		basicHttpAuthenticationHeader.append("Basic ");
		basicHttpAuthenticationHeader.append(base64EncodedString);
		return basicHttpAuthenticationHeader.toString();
	}
	
	public static String decodeApplicationToken(String encodedToken) {
		String[] splitText = encodedToken.split(":");
		return splitText[0];
	}
	
	public static String decodeTimeStamp(String encodedToken) {
		String[] splitText = encodedToken.split(":");
		return splitText[1];
	}
	/**
	 * returns RSA encrypted form of plain token(Converted to base64)
	 * @param plaintextPassword
	 * @return encryptedPassword
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws NoSuchPaddingException
	 */
	public static String encryptToken(String plaintextPassword) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException{
		File privateKeyFile = new File(
				CPConstants.RSA_PPRIVATE_KEY_LOCATION);			
		byte[] tokenBytes = plaintextPassword.getBytes();
		/*String encryptedPassword = new String(
				RSAKeyGenerationUtil.encryptNew(
						passwordBytes, privateKeyFile));*/
		String encryptedToken = Base64.encodeBytes(RSAKeyGenerationUtil.encryptNew(
						tokenBytes, privateKeyFile));
		return encryptedToken;
	}
	
	public static String decryptToken(String encryptedToken) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException{
		File privateKeyFile = new File(
				CPConstants.RSA_PPRIVATE_KEY_LOCATION);	
//		byte[] tokenBytes = encryptedToken.getBytes();
		String plainToken = new String(RSAKeyGenerationUtil.decryptNew(Base64.decode(encryptedToken),privateKeyFile));
		return plainToken;
	}
}
