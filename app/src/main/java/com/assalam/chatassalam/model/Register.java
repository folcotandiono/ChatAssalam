package com.assalam.chatassalam.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by folcotandiono on 2/2/2019.
 */

public class Register {
    @SerializedName("response")
    private String response;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
