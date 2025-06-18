package com.artemyasnik.db.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class PasswordUtil {
    private static final String HASH_ALGORITHM = "SHA-224";
    private static final HexFormat HEX_FORMAT = HexFormat.of();

    public static String hash(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            return HEX_FORMAT.formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Hash algorithm not available", e);
        }
    }

    public static boolean verify(String plainPassword, String hashedPassword) { return hash(plainPassword).equals(hashedPassword); }
}