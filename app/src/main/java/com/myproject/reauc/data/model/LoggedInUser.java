package com.myproject.reauc.data.model;

public class LoggedInUser {

    private static String userId;
    private static String displayName;
    private static int point;

    public static String getUserId() {
        return userId;
    }

    public static String getDisplayName() {
        return displayName;
    }

    public static int getPoint() {
        return point;
    }

    public static void setUserId(String userId) {
        LoggedInUser.userId = userId;
    }

    public static void setDisplayName(String displayName) {
        LoggedInUser.displayName = displayName;
    }

    public static void setPoint(int point) {
        LoggedInUser.point = point;
    }

    public static void logout() {
        userId = null;
        displayName = null;
        point = 0;
    }
}