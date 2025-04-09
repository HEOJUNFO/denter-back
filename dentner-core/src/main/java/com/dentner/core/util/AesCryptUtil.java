package com.dentner.core.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesCryptUtil {

	 // 32바이트 길이 비밀키 (256비트)
    private static final String SECRET_KEY = "A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5PC"; 
    // 16바이트 길이 IV (128비트)
    private static final String IV = "Q1W2E3R4T5Y6U7I8"; 

    // 비밀키와 IV를 바이트 배열로 변환
    private static SecretKeySpec getSecretKeySpec() {
        byte[] keyBytes = SECRET_KEY.getBytes();
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("Invalid key length. Must be 32 bytes.");
        }
        return new SecretKeySpec(keyBytes, "AES");
    }

    private static IvParameterSpec getIvParameterSpec() {
        byte[] ivBytes = IV.getBytes();
        if (ivBytes.length != 16) {
            throw new IllegalArgumentException("Invalid IV length. Must be 16 bytes.");
        }
        return new IvParameterSpec(ivBytes);
    }

    // 암호화된 문자열 복호화
    public static String decrypt(String encryptedText) {
        try {
        	 Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
             cipher.init(Cipher.DECRYPT_MODE, getSecretKeySpec(), getIvParameterSpec());

             byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
             byte[] decryptedBytes = cipher.doFinal(decodedBytes);

             return new String(decryptedBytes, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
