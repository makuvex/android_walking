package com.friendly.walking.util;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


//원문 : 1234abcd
//md5 : ef73781effc5774100f87fe2f437a435
//sha512 : b5fca70016e67c21326094f802a69b092898870ed372744e992353d86a5b3b384a7ca55a076dcc3af3e5b4812e1e365e74a5f8e957481911a70f99a060772648


/**
 * md5, sha512, aes 암호화...
 */
public class Crypto {
	private static final String			CIPHER_TRANSFORMATION	= "AES/ECB/PKCS5Padding";	

	
	/**
	 * message을 md5를 한후 sha512로 변환하여 hex string으로 반환
	 * @return
	 */
	public static String encryptSSL(String msg) throws Exception {
		
		String dataMD5 = md5(msg);
		if(dataMD5 == null) throw new Exception("Encryption Error...");

		String dataSHA512 = sha512(dataMD5);
		if(dataSHA512 == null) throw new Exception("Encryption Error...");

		return dataSHA512;
	}

	public static String encryptSSLSSL(String msg) throws Exception {

		String dataSHA512 = sha512(msg);
		if(dataSHA512 == null) throw new Exception("Encryption Error...");

		String result = sha512(dataSHA512);
		
		return result;

	}
	
	/**
	 * sha512 암호화
	 * @param msg
	 * @return
	 */
	public static String sha512(String msg) {
		
		return hash("SHA-512", msg);
	}
		
	/**
	 * md5 암호화
	 * @param msg
	 * @return
	 */	
	public static String md5(String msg) {

		return hash("MD5", msg);
	}

	private static String hash(String type, String msg) {
		if(msg == null) return null;
		
		MessageDigest md = null;  

        try {  
            md = MessageDigest.getInstance(type);  
              
            md.update(msg.getBytes());  

            return byteArrayToHex(md.digest());

        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }

        return null;
	}

	/**
	 * hex를 byte array로 변환
	 * @param hex
	 * @return
	 */
    public static byte[] hexToByteArray(String hex) {
        if (hex == null || hex.length() == 0) {
            return null;
        }
     
        byte[] ba = new byte[hex.length() / 2];
        for (int i = 0; i < ba.length; i++) {
            ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        
        return ba;
    }

    /**
     * byte array를 hex로 변환
     * @param ba
     * @return
     */
    public static String byteArrayToHex(byte[] ba) {
        if (ba == null || ba.length == 0) {
            return null;
        }
     
        StringBuffer sb = new StringBuffer(ba.length * 2);
        String hexNumber;
        for (int x = 0; x < ba.length; x++) {
            hexNumber = "0" + Integer.toHexString(0xff & ba[x]);
     
            sb.append(hexNumber.substring(hexNumber.length() - 2));
        }
        
        return sb.toString();
    } 

	/**
	 * 키 생성
	 */
	private static SecretKeySpec createKeySpec(String secureKey){
		SecretKeySpec localKeySpec = null;
		String devId = secureKey;			// device Id가 없는 경우 특정 고정 문자열을 키로 사용한다.
		if(TextUtils.isEmpty(devId)){
			devId = Settings.Secure.ANDROID_ID;
		}
		
    	if(devId.length() != 16){
    		if(devId.length() > 16){
    			devId = devId.substring(0, 16);
    		} else {
    			int j =0;
    			for(int i = devId.length(); i< 16; i++){
    				devId += j;
    				j++;
    				if(j == 10){
    					j = 0;
    				}
    			}
    		}
    	}

		try {
			localKeySpec = new SecretKeySpec(devId.getBytes(), "AES");
			
			return localKeySpec;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * AES 방식의 암호화
	 *
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public static String encryptAES(String message, String key) throws Exception {

		if(TextUtils.isEmpty(message))
			return message;

		// use key coss2
		SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");

		// Instantiate the cipher
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

		byte[] encrypted = cipher.doFinal(message.getBytes());
		return byteArrayToHex(encrypted);
	}

	/**
	 * AES 방식의 복호화
	 *
	 * @param encrypted
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String decryptAES(String encrypted, String key) throws Exception {

		if(TextUtils.isEmpty(encrypted))
			return encrypted;

		// use key coss2
		SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");

		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] original = cipher.doFinal(hexToByteArray(encrypted));
		String originalString = new String(original);
		return originalString;
	}
}