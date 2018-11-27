package com.game.rk6cooperation.androidgame;

public class UserHolder {
    private static UserHolder cookieHolder;

    private String cookie;
    private String nickname;

    private UserHolder() {
    }

    public static UserHolder getUserHolder() {
        if (cookieHolder == null) {
            cookieHolder = new UserHolder();
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

    public String getNickname() {
        return cookieHolder.nickname;
    }

    public void setNickname(String nickname) {
        if (cookieHolder != null) {
            cookieHolder.nickname = nickname;
        }
    }

    public void deleteCookie() {
        if (cookieHolder != null) {
            cookieHolder.cookie = null;
        }
    }

    public Boolean isCookieExist() {
        if (cookieHolder != null) {
            if(cookie != null)
                return true;
        }
        return false;
    }
}
