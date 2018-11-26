package com.game.rk6cooperation.androidgame.Network;

import java.util.ArrayList;
import java.util.List;

public class RegUserResponse {

    private String status = "";
    private List<String> errors = new ArrayList<>();
    private User user = new User();

    public class User {
        private String nickname = "";

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
