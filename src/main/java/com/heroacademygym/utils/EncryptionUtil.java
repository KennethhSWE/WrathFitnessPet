package com.heroacademygym.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptionUtil {
    private static final Logger logger = LoggerFactory.getLogger(EncryptionUtil.class);
    private static final String ALGORITHM = "AES";
    private static final String encryptionKey = System.getenv("ENCRYPTION_KEY");

    private static void validateEncryptionKey() {
        logger.info("ENCRYPTION_KEY length: " + (encryptionKey != null ? encryptionKey.length() : "null"));
        if (encryptionKey == null || encryptionKey.length() !=16) {
            throw new IllegalArgumentException("Invalid ENCRYPTION_KEY length. It must be 16 characters long.");
        } 
    }

    //Method used to encrypt the Strava data
    public static String encrypt(String data) throws Exception {
        validateEncryptionKey();
        SecretKey key = new SecretKeySpec(encryptionKey.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData); 
    }

    //Method used to Decrypt the Strava data
    public static String decrypt(String encryptedData) throws Exception {
        validateEncryptionKey();
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] originalData = cipher.doFinal(decodedData);
        return new String(originalData);
    }
    
}
