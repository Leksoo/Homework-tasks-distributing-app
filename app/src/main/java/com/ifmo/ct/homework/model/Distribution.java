package com.ifmo.ct.homework.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

public class Distribution {

    private List<String> studentNames;

    private List<Integer> studentPoints;

    private List<Integer> taskList;

    private List<List<Integer>> distributionTable;

    public Distribution() {
    }

    public Distribution(List<String> studentNames, List<Integer> studentPoints, List<Integer> taskList, List<List<Integer>> distributionTable) {
        this.studentNames = studentNames;
        this.studentPoints = studentPoints;
        this.taskList = taskList;
        this.distributionTable = distributionTable;
    }

    public int getStatus(int i, int j) {
        return distributionTable.get(i).get(j);
    }

    public void setStatus(int i, int j, int status) {
        distributionTable.get(i).set(j, status);
    }

    public List<String> getStudentNames() {
        return studentNames;
    }

    public List<Integer> getStudentPoints() {
        return studentPoints;
    }

    public List<Integer> getTaskList() {
        return taskList;
    }

    public List<List<Integer>> getDistributionTable() {
        return distributionTable;
    }

    public void setStudentNames(List<String> studentNames) {
        this.studentNames = studentNames;
    }

    public void setStudentPoints(List<Integer> studentPoints) {
        this.studentPoints = studentPoints;
    }

    public void setTaskList(List<Integer> taskList) {
        this.taskList = taskList;
    }

    public void setDistributionTable(List<List<Integer>> distributionTable) {
        this.distributionTable = distributionTable;
    }
}
