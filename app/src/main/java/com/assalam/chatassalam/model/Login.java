package com.assalam.chatassalam.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by folcotandiono on 2/2/2019.
 */

public class Login {
    @SerializedName("response")
    private String response;
    @SerializedName("id_user")
    private String idUser;
    @SerializedName("username")
    private String username;
    @SerializedName("nama")
    private String nama;
    @SerializedName("no_hp")
    private String noHp;
    @SerializedName("jenis_kelamin")
    private String jenisKelamin;
    @SerializedName("tanggal_lahir")
    private String tanggalLahir;
    @SerializedName("status_taaruf")
    private String statusTaaruf;
    @SerializedName("gambar")
    private String gambar;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public String getTanggalLahir() {
        return tanggalLahir;
    }

    public void setTanggalLahir(String tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatusTaaruf() {
        return statusTaaruf;
    }

    public void setStatusTaaruf(String statusTaaruf) {
        this.statusTaaruf = statusTaaruf;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
}
