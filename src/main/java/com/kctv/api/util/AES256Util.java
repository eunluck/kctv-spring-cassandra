package com.kctv.api.util;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 양방향 암호화 알고리즘인 AES256 암호화를 지원하는 클래스
 */
@Component
public class AES256Util {
    private static String iv;
    private static Key keySpec;
    private static AES256Util instance;

    /**

    /**
     * 16자리의 키값을 입력하여 객체를 생성한다.
     *
     * @param key
     *            암/복호화를 위한 키값
     * @throws UnsupportedEncodingException
     *             키값의 길이가 16이하일 경우 발생
     */
    final static String key = "!@kctvjeju&wakeuF2021@#*q1w2e3r4";





    public AES256Util() throws UnsupportedEncodingException {
        this.iv = key.substring(0, 16);
        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes("UTF-8");
        int len = b.length;
        if (len > keyBytes.length) {
            len = keyBytes.length;
        }
        System.arraycopy(b, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        this.keySpec = keySpec;
    }

    public static AES256Util getInstance() throws UnsupportedEncodingException {
        if (instance == null) {
            instance = new AES256Util();
        }
        return instance;
    }

    /**
     * AES256 으로 암호화 한다.
     *
     * @param str
     *            암호화할 문자열
     * @return
     * @throws NoSuchAlgorithmException
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    public String encrypt(String str) throws NoSuchAlgorithmException,
            GeneralSecurityException, UnsupportedEncodingException {
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
        byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
        String enStr = new String(Base64.encodeBase64(encrypted));
        return enStr;
    }

    /**
     * AES256으로 암호화된 txt 를 복호화한다.
     *
     * @param str
     *            복호화할 문자열
     * @return
     * @throws NoSuchAlgorithmException
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    public String decrypt(String str) throws NoSuchAlgorithmException,
            GeneralSecurityException, UnsupportedEncodingException {
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
        byte[] byteStr = Base64.decodeBase64(str.getBytes());
        return new String(c.doFinal(byteStr), "UTF-8");
    }

    public boolean isBase64(String str){


        return str.matches("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$");
    }
}