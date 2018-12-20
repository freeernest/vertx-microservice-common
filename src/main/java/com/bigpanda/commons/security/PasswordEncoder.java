package com.bigpanda.commons.security;

import java.security.MessageDigest;

/**
 * Created by erik on 3/8/18.
 */
public class PasswordEncoder {

    public static String encodePassword(String rawPass, Object salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            String salted = rawPass + "{" + salt.toString() + "}";

            md.update(salted.getBytes("UTF-8"));
            byte[] digest = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }

            return sb.toString();

        } catch (Exception e) {
            return null;
        }
    }
}
