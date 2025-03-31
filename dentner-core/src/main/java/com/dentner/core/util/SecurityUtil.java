package com.dentner.core.util;

import com.dentner.core.config.CustomUserDetails;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class SecurityUtil {



    public static String getMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadCredentialsException("인증되지 않은 사용자의 접근 시도입니다.");
        }
        Object principal = authentication.getPrincipal();

        // principal 객체가 CustomUserDetails 인스턴스인지 확인합니다.
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails user = (CustomUserDetails) principal;
            return user.getUsername();
        } else {
            throw new IllegalStateException("Principal 객체가 CustomUserDetails 타입이 아닙니다.");
        }
    }

    public static String getMemberSe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadCredentialsException("인증되지 않은 사용자의 접근 시도입니다.");
        }
        Object principal = authentication.getPrincipal();

        // principal 객체가 CustomUserDetails 인스턴스인지 확인합니다.
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails user = (CustomUserDetails) principal;
            return user.getEx();
        } else {
            throw new IllegalStateException("Principal 객체가 CustomUserDetails 타입이 아닙니다.");
        }
    }

    public static int getMemberNo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadCredentialsException("인증되지 않은 사용자의 접근 시도입니다.");
        }
        Object principal = authentication.getPrincipal();

        // principal 객체가 CustomUserDetails 인스턴스인지 확인합니다.
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails user = (CustomUserDetails) principal;
            return Integer.parseInt(user.getUsername());
        } else {
            throw new IllegalStateException("Principal 객체가 CustomUserDetails 타입이 아닙니다.");
        }
    }

    public static String encrypt(String val) {
        try {
            SecretKeySpec key = new SecretKeySpec(EncryptionUtil.secretKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(val.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String encryptedVal) {
        try {
            SecretKeySpec key = new SecretKeySpec(EncryptionUtil.secretKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedVal);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
