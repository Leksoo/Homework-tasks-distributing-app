package com.ifmo.ct.homework.model;


import com.squareup.moshi.Json;

public class GoogleSheet {
    @Json(name = "version")
    private String version;
    @Json(name = "reqId")
    private String reqId;
    @Json(name = "status")
    private String status;
    @Json(name = "sig")
    private String sig;
    @Json(name = "table")
    private Table table;

    /**
     * No args constructor for use in serialization
     *
     */
    public GoogleSheet() {
    }

    /**
     *
     * @param reqId
     * @param status
     * @param table
     * @param sig
     * @param version
     */
    public GoogleSheet(String version, String reqId, String status, String sig, Table table) {
        super();
        this.version = version;
        this.reqId = reqId;
        this.status = status;
        this.sig = sig;
        this.table = table;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }
}
