package com.monza.app.service;

public class PermissionService {

    public static boolean canEditPost(Long postAuthorId, Long currentUserId, String currentUserRole) {
        if (isAdministrator(currentUserRole)) {
            return true;
        }
        return postAuthorId.equals(currentUserId);
    }

    public static boolean canDeletePost(Long postAuthorId, Long currentUserId, String currentUserRole) {
        if (isAdministrator(currentUserRole)) {
            return true;
        }
        return postAuthorId.equals(currentUserId);
    }

    public static boolean canEditThread(Long threadAuthorId, Long currentUserId, String currentUserRole) {
        if (isAdministrator(currentUserRole)) {
            return true;
        }
        return threadAuthorId.equals(currentUserId);
    }

    public static boolean canDeleteThread(Long threadAuthorId, Long currentUserId, String currentUserRole) {
        if (isAdministrator(currentUserRole)) {
            return true;
        }
        return threadAuthorId.equals(currentUserId);
    }

    public static boolean canPinThread(String currentUserRole) {
        return isAdministrator(currentUserRole);
    }

    public static boolean canLockThread(String currentUserRole) {
        return isAdministrator(currentUserRole);
    }

    private static boolean isAdministrator(String role) {
        return "ADMIN".equals(role);
    }
}

// -