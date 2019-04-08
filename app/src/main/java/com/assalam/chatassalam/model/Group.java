package com.assalam.chatassalam.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by folcotandiono on 3/18/2019.
 */

public class Group {
    @SerializedName("id_group")
    private String idGroup;
    @SerializedName("nama")
    private String nama;
    @SerializedName("gambar")
    private String gambar;
    @SerializedName("tanggal")
    private String tanggal;

    public String getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(String idGroup) {
        this.idGroup = idGroup;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
