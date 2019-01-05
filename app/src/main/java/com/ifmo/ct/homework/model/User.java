package com.ifmo.ct.homework.model;

import java.util.ArrayList;
import java.util.List;

public class User {

    public static final int TEACHER_TYPE = 1;
    public static final int STUDENT_TYPE = 2;

    private String name;
    private int type;

    public User() {
    }

    public User(String name, int type) {
        this.name = name;
        this.type = type;
    }


    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }



}
