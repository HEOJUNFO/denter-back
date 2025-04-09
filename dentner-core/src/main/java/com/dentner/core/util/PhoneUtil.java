package com.dentner.core.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Random;

import static org.apache.ibatis.io.Resources.getResourceAsStream;

public class PhoneUtil {

    public static String generateOTP(int length) {
        String numbers = "0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(numbers.length());
            char digit = numbers.charAt(index);
            sb.append(digit);
        }
        // 생성된 숫자를 문자열로 반환
        return sb.toString();
    }
}
