package com.manura.foodapp.shared.Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {
    public static String PLAIN_TEXT = "SS18617710213463";
    public static String ENCRYPTION_KEY = "mykey@91mykey@91";
    static String INITIALIZATIO_VECTOR = "AODVNUASDNVVAOVF";

    public static byte[] encrypt(String plainText, String encryptionKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/pkcs5padding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(INITIALIZATIO_VECTOR.getBytes("UTF-8")));
        return cipher.doFinal(plainText.getBytes("UTF-8"));
    }

    public static String decrypt(byte[] cipherText, String encryptionKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/pkcs5padding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(INITIALIZATIO_VECTOR.getBytes("UTF-8")));
        return new String(cipher.doFinal(cipherText), "UTF-8");
    }
}
