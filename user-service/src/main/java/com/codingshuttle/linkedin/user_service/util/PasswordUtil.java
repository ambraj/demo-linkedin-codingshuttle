package com.codingshuttle.linkedin.user_service.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // hash password using BCrypt
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // verify password using BCrypt
    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

}
