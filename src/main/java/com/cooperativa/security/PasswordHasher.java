package com.cooperativa.security;

import org.mindrot.jbcrypt.BCrypt;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordHasher {

    private PasswordHasher() {}

    /**
     * Genera un hash SHA-256 de exactamente 64 caracteres compatible con CHAR(64) en MySQL.
     */
    public static String hashSHA256(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al inicializar algoritmo SHA-256", e);
        }
    }

    /**
     * Verifica una contraseña en texto plano contra un hash almacenado (soporta SHA-256 y BCrypt).
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }
        String trimmedHash = storedHash.trim();
        if (trimmedHash.startsWith("$2a$") || trimmedHash.startsWith("$2b$")) {
            return BCrypt.checkpw(plainPassword, trimmedHash);
        }
        return hashSHA256(plainPassword).equalsIgnoreCase(trimmedHash);
    }
}