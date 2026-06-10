package com.gym.crm.application.context;

public class SecurityContextHolder {
    private static final ThreadLocal<String> currentUsername = new ThreadLocal<>();

    public static void setContext(String username) { currentUsername.set(username); }
    public static String getContext() { return currentUsername.get(); }
    public static void clear() { currentUsername.remove(); }
}
