package com.assalam.chatassalam.model;

/**
 * Created by folcotandiono on 3/29/2019.
 */

public class ChatHeader {
    private String header;
    private String nama;
    private String pesan_terakhir;
    private String waktu;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getPesan_terakhir() {
        return pesan_terakhir;
    }

    public void setPesan_terakhir(String pesan_terakhir) {
        this.pesan_terakhir = pesan_terakhir;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }
}
