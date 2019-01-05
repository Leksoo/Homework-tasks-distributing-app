package com.ifmo.ct.homework.model;

import com.squareup.moshi.Json;

public class Col {
    @Json(name = "id")
    private String id;
    @Json(name = "label")
    private String label;
    @Json(name = "type")
    private String type;
    @Json(name = "pattern")
    private String pattern;

    /**
     * No args constructor for use in serialization
     *
     */
    public Col() {
    }

    /**
     *
     * @param id
     * @param pattern
     * @param label
     * @param type
     */
    public Col(String id, String label, String type, String pattern) {
        super();
        this.id = id;
        this.label = label;
        this.type = type;
        this.pattern = pattern;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

}

