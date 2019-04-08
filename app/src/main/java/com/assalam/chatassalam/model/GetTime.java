package com.assalam.chatassalam.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by folcotandiono on 3/4/2019.
 */

public class GetTime {
    @SerializedName("tanggal")
    private String tanggal;

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
