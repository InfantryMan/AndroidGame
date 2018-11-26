package com.game.rk6cooperation.androidgame.Network;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardUsers {

    private List<ScoreboardUser> users = new ArrayList<>();

    private String status = "";

    private Integer pages = 0;

    public List<ScoreboardUser> getUsers() {
        return users;
    }

    public void setUsers(List<ScoreboardUser> users) {
        this.users = users;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

}
