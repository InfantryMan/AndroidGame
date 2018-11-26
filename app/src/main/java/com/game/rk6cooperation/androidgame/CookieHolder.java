package com.game.rk6cooperation.androidgame;

public class CookieHolder {
    private static CookieHolder cookieHolder;

    private String cookie;

    private CookieHolder() {
    }

    public static CookieHolder getCookieHolder() {
        if (cookieHolder == null) {
            cookieHolder = new CookieHolder();
        }
        return cookieHolder;
    }

    public String getCookie() {
        if (cookieHolder.cookie != null) {
            return cookieHolder.cookie;
        } else return null;
    }

    public void setCookie(String cookie) {
        if (cookieHolder != null) {
            cookieHolder.cookie = cookie;
        }
    }
}
