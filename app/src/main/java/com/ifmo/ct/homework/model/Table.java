package com.ifmo.ct.homework.model;

import com.squareup.moshi.Json;

import java.util.List;

public class Table {
    @Json(name = "cols")
    private List<Col> cols = null;
    @Json(name = "rows")
    private List<Row> rows = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public Table() {
    }

    /**
     *
     * @param cols
     * @param rows
     */
    public Table(List<Col> cols, List<Row> rows) {
        super();
        this.cols = cols;
        this.rows = rows;
    }

    public List<Col> getCols() {
        return cols;
    }

    public void setCols(List<Col> cols) {
        this.cols = cols;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }
}
