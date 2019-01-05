package com.ifmo.ct.homework.model;

import com.ifmo.ct.homework.utils.SheetPrefs;

public class SheetRefs {
    private String ADS;
    private String DM;

    public SheetRefs() {
    }

    public SheetRefs(String ADS, String DM) {
        this.ADS = ADS;
        this.DM = DM;
    }

    public String getADS() {
        return ADS;
    }

    public void setADS(String ADS) {
        this.ADS = ADS;
    }

    public String getDM() {
        return DM;
    }

    public void setDM(String DM) {
        this.DM = DM;
    }
}
