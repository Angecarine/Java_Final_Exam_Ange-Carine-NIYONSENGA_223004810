package com.utils;

import java.security.MessageDigest;

public class Utils {
    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String hexString = Integer.toHexString(0xff & b);
                if (hexString.length() == 1) hex.append('0');
                hex.append(hexString);
            }
            return hex.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null; // <- could cause NPE if returned null
        }
    }
}
