package com.accedo.wynkstudio.util;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import net.iharder.Base64;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;

import com.accedo.wynkstudio.common.CPConstants;

public class RSAKeyGenerationUtil {

	private static Key publicKey;
	private static Key privateKey;
	

	private static String strPublicKey;
	private static String strPrivateKey;
	

	/** Our split character. */
    protected static final char SPLIT = '#';
    

	public static String getStrPublicKey() {
		return strPublicKey;
	}

	public static void setStrPublicKey(String strPublicKey) {
		RSAKeyGenerationUtil.strPublicKey = strPublicKey;
	}

	public static String getStrPrivateKey() {
		return strPrivateKey;
	}

	public static void setStrPrivateKey(String strPrivateKey) {
		RSAKeyGenerationUtil.strPrivateKey = strPrivateKey;
	}
	
	public static Key getPublicKey() {
		return publicKey;
	}

	public static void setPublicKey(Key publicKey) {
		RSAKeyGenerationUtil.publicKey = publicKey;
	}

	public static Key getPrivateKey() {
		return privateKey;
	}

	public static void setPrivateKey(Key privateKey) {
		RSAKeyGenerationUtil.privateKey = privateKey;
	}
	
	public static File readPrivateKeyFile(){
		return new File(
				CPConstants.RSA_PPRIVATE_KEY_LOCATION);
	}

	//Generate private and public key
	public static void generateKeyNew() throws NoSuchAlgorithmException, 
	NoSuchPaddingException, InvalidKeySpecException, IOException {
		KeyFactory fact = KeyFactory.getInstance("RSA"); 
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(1024); // 1024 used for normal

		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();
		setPublicKey(keyPair.getPublic());
		setPrivateKey(keyPair.getPrivate());
		
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		
		RSAPrivateKeySpec rsaPrivKeySpec = fact.getKeySpec(privateKey, 
                 RSAPrivateKeySpec.class);
		System.out.println("Writing private key...");
		fos = new FileOutputStream("D:\\privatekey.txt");
		oos = new ObjectOutputStream(new BufferedOutputStream(fos));
		oos = new ObjectOutputStream(new BufferedOutputStream(fos));
		oos.writeObject(rsaPrivKeySpec.getModulus());
		oos.writeObject(rsaPrivKeySpec.getPrivateExponent());
		oos.close();
		
		RSAPublicKeySpec rsaPublicKeySpec = fact.getKeySpec(publicKey, 
                RSAPublicKeySpec.class);
		System.out.println("Writing public key...");
		fos = new FileOutputStream("D:\\publickey.txt");
		oos = new ObjectOutputStream(new BufferedOutputStream(fos));
		oos = new ObjectOutputStream(new BufferedOutputStream(fos));
		oos.writeObject(rsaPublicKeySpec.getModulus());
		oos.writeObject(rsaPublicKeySpec.getPublicExponent());
		oos.close();
		
		strPublicKey = RSAKeyToString(publicKey);
		strPrivateKey = RSAKeyToString(privateKey);

	}
	
	/**
     * Converts an key to a string suitable for a properties file.
     */
    public static String RSAKeyToString (PublicKey key)
    {
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec spec = kf.getKeySpec(key, RSAPublicKeySpec.class);
            StringBuilder buf = new StringBuilder();
            buf.append(spec.getModulus().toString(16))
                .append(SPLIT)
                .append(spec.getPublicExponent().toString(16));
            return buf.toString();
        } catch (Exception gse) {
            System.out.println("Failed to convert key to string" + gse.getMessage());
        }
        return null;
    }

    /**
     * Converts an key to a string suitable for a properties file.
     */
    public static String RSAKeyToString (PrivateKey key)
    {
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            RSAPrivateKeySpec spec = kf.getKeySpec(key, RSAPrivateKeySpec.class);
            StringBuilder buf = new StringBuilder();
            buf.append(spec.getModulus().toString(16))
                .append(SPLIT)
                .append(spec.getPrivateExponent().toString(16));
            return buf.toString();
        } catch (Exception gse) {
            System.out.println("Failed to convert key to string" + gse.getMessage());
        }
        return null;
    }
    
    /**
     * Creates a private key from the supplied string.
     */
    public static PrivateKey stringToRSAPrivateKey (String str)
    {
        try {
            BigInteger mod = new BigInteger(str.substring(0, str.indexOf(SPLIT)), 16);
            BigInteger exp = new BigInteger(str.substring(str.indexOf(SPLIT) + 1), 16);
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(mod, exp);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(keySpec);
        } catch (NumberFormatException nfe) {
        	 System.out.println("Failed to read key from string str = "+ str +" "+ nfe.getMessage());
        } catch (Exception gse) {
        	System.out.println("Failed to read key from string str = "+ str +" "+ gse.getMessage());
        }
        return null;
    }
    
    /**
     * Creates a public key from the supplied string.
     */
    public static PublicKey stringToRSAPublicKey (String str)
    {
        try {
            BigInteger mod = new BigInteger(str.substring(0, str.indexOf(SPLIT)), 16);
            BigInteger exp = new BigInteger(str.substring(str.indexOf(SPLIT) + 1), 16);
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(mod, exp);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(keySpec);
        } catch (NumberFormatException nfe) {
       	 System.out.println("Failed to read key from string str = "+ str +" "+ nfe.getMessage());
       } catch (Exception gse) {
       	System.out.println("Failed to read key from string str = "+ str +" "+ gse.getMessage());
       }
        return null;
    }
	
	//Encrypt
	public static byte[] encryptNew(byte[] input, File filePrivateKey)
			throws IllegalBlockSizeException, BadPaddingException,
			InvalidKeyException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException {
		Security.addProvider(new BouncyCastleProvider());
		Cipher cipher = Cipher.getInstance("RSA");
		Key publicKey = null;
		try {
			KeyPair keyPair = readKeyPair(filePrivateKey);
			publicKey = keyPair.getPublic();
		} catch (IOException e) {
			e.printStackTrace();
		}
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] cipherText = cipher.doFinal(input);
		System.out.println("cipher: " + new String(cipherText));
		return cipherText;
	}
	
	
	
	//Decrypt
	public static byte[] decryptNew(byte[] cipherText, File filePrivateKey)
			throws NoSuchAlgorithmException, NoSuchProviderException,
			NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		Security.addProvider(new BouncyCastleProvider());
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		Key privateKey = null;
		try {
			KeyPair keyPair = readKeyPair(filePrivateKey);
			privateKey = keyPair.getPrivate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] retrievedPlainText = cipher.doFinal(cipherText);
		return retrievedPlainText;
	}
	
	

	public static void generateKey() throws NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		SecureRandom random = new SecureRandom();
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");		
		generator.initialize(256, random);

		KeyPair pair = generator.generateKeyPair();
		setPublicKey(pair.getPublic());
		setPrivateKey(pair.getPrivate());
	}

	public static String keyToString(Key key) {
		return Base64.encodeBytes(key.getEncoded());

	}

	public static byte[] stringKeyToByteArray(String keyAsString)
			throws IOException {
		return Base64.decode(keyAsString);
	}

	public static byte[] keyToByteArray(Key key) throws IOException {
		String keyAsString = Base64.encodeBytes(key.getEncoded());
		byte[] bytes = Base64.decode(keyAsString);
		return bytes;
	}

	public static Key byteArrayToPrivateKey(byte[] bytes)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(bytes);
		KeyFactory rsaKeyFac = KeyFactory.getInstance("RSA");
		Key privateKey = rsaKeyFac.generatePrivate(encodedKeySpec);
		return privateKey;
	}

	public static Key byteArrayToPublicKey(byte[] bytes)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(bytes);
		KeyFactory rsaKeyFac = KeyFactory.getInstance("RSA");
		Key pubKey = rsaKeyFac.generatePublic(encodedKeySpec);
		return pubKey;
	}

	public static byte[] decrypt(byte[] cipherText)
			throws NoSuchAlgorithmException, NoSuchProviderException,
			NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		Cipher cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] retrievedPlainText = cipher.doFinal(cipherText);
		return retrievedPlainText;
	}

	 public static byte[] encrypt(byte[] input) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException{
	 Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	 SecureRandom random = new SecureRandom();
	 Cipher cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
	 cipher.init(Cipher.ENCRYPT_MODE, publicKey, random);
	 byte[] cipherText = cipher.doFinal(input);
	 System.out.println("cipher: " + new String(cipherText));
	 return cipherText;
	 }
	 
	 public static void init() throws NoSuchAlgorithmException, NoSuchProviderException{
		 Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			SecureRandom random = new SecureRandom();
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");		
			generator.initialize(256, random);
	 }
	 
	 private static KeyPair readKeyPair(File privateKey) throws IOException {
	        FileReader fileReader = new FileReader(privateKey);
	        PEMReader r = new PEMReader(fileReader);
	        try {
	            return (KeyPair) r.readObject(); // this returns null
	        } catch (IOException ex) {
	            throw new IOException("The private key could not be decrypted", ex);
	        } finally {
	            r.close();
	            fileReader.close();
	        }
	    }
		 

}
