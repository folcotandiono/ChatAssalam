package com.assalam.chatassalam.model;

import com.google.gson.annotations.SerializedName;

public class CheckFriend {
    @SerializedName("status")
    private String status;
    @SerializedName("friend")
    private String friend;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }
}
