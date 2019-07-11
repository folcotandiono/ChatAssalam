package com.assalam.chatassalam.model;

import com.google.gson.annotations.SerializedName;

public class EditProfilTaaruf {
    @SerializedName("nama")
    private String nama;
    @SerializedName("jenis_kelamin")
    private String jenisKelamin;
    @SerializedName("tanggal_lahir")
    private String tanggalLahir;
    @SerializedName("gambar")
    private String gambar;

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
}
