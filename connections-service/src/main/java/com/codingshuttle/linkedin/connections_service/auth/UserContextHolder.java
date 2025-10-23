package com.codingshuttle.linkedin.connections_service.auth;

public class UserContextHolder {

    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();

    public static void setCurrentUserId(Long userId) {
        UserContextHolder.currentUserId.set(userId);
    }

    public static Long getCurrentUserId() {
        return UserContextHolder.currentUserId.get();
    }

    public static void clear() {
        currentUserId.remove();
    }

}
