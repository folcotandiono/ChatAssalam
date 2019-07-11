package com.assalam.chatassalam;

import com.google.gson.annotations.SerializedName;

public class TergabungDiGroup {
    @SerializedName("tergabung")
    private Boolean tergabung;
    @SerializedName("nama_group")
    private String namaGroup;

    public Boolean getTergabung() {
        return tergabung;
    }

    public void setTergabung(Boolean tergabung) {
        this.tergabung = tergabung;
    }

    public String getNamaGroup() {
        return namaGroup;
    }

    public void setNamaGroup(String namaGroup) {
        this.namaGroup = namaGroup;
    }
}
