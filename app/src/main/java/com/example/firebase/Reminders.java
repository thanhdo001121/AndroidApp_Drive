package com.example.firebase;

public class Reminders {


    String task,remindDate,id;

    public Reminders(String task, String remindDate, String id) {
        this.task = task;
        this.remindDate = remindDate;
        this.id = id;
    }

    public Reminders(String task, String remindDate) {
        this.task = task;
        this.remindDate = remindDate;
    }

    public Reminders() {
        this.task = "";
        this.remindDate = "";
    }

    public String getTask() {
        return task;
    }

    public String getRemindDate() {
        return remindDate;
    }

    public String getId() {
        return id;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setRemindDate(String remindDate) {
        this.remindDate = remindDate;
    }

    public void setId(String id) {
        this.id = id;
    }
}

