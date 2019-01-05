package com.ifmo.ct.homework.model;

public class Teacher extends User {

    public Teacher() {
    }

    public Teacher(String name) {
        super(name, User.TEACHER_TYPE);
    }
}
