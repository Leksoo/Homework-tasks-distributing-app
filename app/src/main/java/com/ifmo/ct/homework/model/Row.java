package com.ifmo.ct.homework.model;


import com.google.gson.internal.LinkedHashTreeMap;
import com.squareup.moshi.Json;

import java.util.List;

public class Row {
    @Json(name = "c")
    private List<Object> c = null;

    /**
     * No args constructor for use in serialization
     */
    public Row() {
    }

    /**
     * @param c
     */
    public Row(List<Object> c) {
        super();
        this.c = c;
    }

    public List<Object> getC() {
        return c;
    }

    public void setC(List<Object> c) {
        this.c = c;
    }
}
