package com.ifmo.ct.homework.model;

import com.ifmo.ct.homework.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Student extends User {

    private String group;
    private int pointsDM;
    private int pointsADS;
    private List<Integer> taskListDM;
    private List<Integer> taskListADS;

    public Student() {
        taskListADS = new ArrayList<>();
        taskListDM = new ArrayList<>();
    }

    public Student(String name, String group) {
        super(name, User.STUDENT_TYPE);
        this.group = group;
        taskListADS = new ArrayList<>();
        taskListDM = new ArrayList<>();
        pointsADS = 0;
        pointsDM = 0;
    }


    public int getPointsDM() {
        return pointsDM;
    }

    public void setPointsDM(int pointsDM) {
        this.pointsDM = pointsDM;
    }

    public int getPointsADS() {
        return pointsADS;
    }

    public void setPointsADS(int pointsADS) {
        this.pointsADS = pointsADS;
    }

    public String getGroup() {
        return group;
    }

    public int getPoints(int subjectCode){
        return subjectCode == MainActivity.ADS_CODE ? pointsADS : pointsDM;
    }

    public List<Integer> getTaskList(int subjectCode) {
        return subjectCode == MainActivity.ADS_CODE ? taskListADS : taskListDM;
    }

    public void setTaskList(List<Integer> taskList, int subjectCode) {
        if(subjectCode == MainActivity.ADS_CODE ) {
            taskListADS = taskList;
        }
        else {
            taskListDM = taskList;
        }
    }

    public List<Integer> getTaskListDM() {
        return taskListDM;
    }

    public void setTaskListDM(List<Integer> taskListDM) {
        this.taskListDM = taskListDM;
    }

    public List<Integer> getTaskListADS() {
        return taskListADS;
    }

    public void setTaskListADS(List<Integer> taskListADS) {
        this.taskListADS = taskListADS;
    }
}
