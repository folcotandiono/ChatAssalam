package com.assalam.chatassalam.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by folcotandiono on 4/30/2019.
 */

public class StatusTaaruf {
    @SerializedName("status_taaruf")
    private String statusTaaruf;

    public String getStatusTaaruf() {
        return statusTaaruf;
    }

    public void setStatusTaaruf(String statusTaaruf) {
        this.statusTaaruf = statusTaaruf;
    }
}
