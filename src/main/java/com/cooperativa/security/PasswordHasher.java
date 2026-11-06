package com.cooperativa.security;

import org.mindrot.jbcrypt.BCrypt;

public final class  PasswordHasher {

    private PasswordHasher(){}

    public static String hashPassword(String plainPassword){
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null){
            return false;
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}