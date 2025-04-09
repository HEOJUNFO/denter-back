package com.dentner.core.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EncryptionUtil {

    public static String secretKey;
    public static String secretAesKey;

    @Value("${dentner-secret-key}")
    public void setKey(String value) {
        EncryptionUtil.secretKey = value;
    }

    @Value("${aes.secret-key}")
    public void setAesKey(String value) {
        EncryptionUtil.secretAesKey = value;
    }

}
