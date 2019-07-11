package com.assalam.chatassalam.model;

import com.google.gson.annotations.SerializedName;

public class Teman {
    @SerializedName("id_teman")
    private String idTeman;

    public String getIdTeman() {
        return idTeman;
    }

    public void setIdTeman(String idTeman) {
        this.idTeman = idTeman;
    }
}
