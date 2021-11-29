package com.example.firebase.history;

import java.util.ArrayList;

public class History {
    private String historyId;
    private String actionFlag;
    private String actionDate;
    private String repairType;
    private int lastOdometer;
    private int price;
    private int gallons;
    private String location;
    private String note;

    public History() {
    }

    // Constructor for actions GAS and OIL
    public History(String historyId,String actionFlag, String actionDate, int lastOdometer, int price, int gallons, String location, String note) {
        this.historyId = historyId;
        this.actionFlag = actionFlag;
        this.actionDate = actionDate;
        this.lastOdometer = lastOdometer;
        this.price = price;
        this.gallons = gallons;
        this.location = location;
        this.note = note;
    }

    // Constructor for action REPAIR
    public History(String historyId ,String actionFlag, String actionDate, String repairType, int price, String location, String note) {
        this.historyId = historyId;
        this.actionFlag = actionFlag;
        this.actionDate = actionDate;
        this.repairType = repairType;
        this.price = price;
        this.location = location;
        this.note = note;
    }

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public String getActionFlag() {
        return actionFlag;
    }

    public void setActionFlag(String actionFlag) {
        this.actionFlag = actionFlag;
    }

    public String getActionDate() {
        return actionDate;
    }

    public void setActionDate(String actionDate) {
        this.actionDate = actionDate;
    }

    public String getRepairType() {
        return repairType;
    }

    public void setRepairType(String repairType) {
        this.repairType = repairType;
    }

    public int getLastOdometer() {
        return lastOdometer;
    }

    public void setLastOdometer(int lastOdometer) {
        this.lastOdometer = lastOdometer;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getGallons() {
        return gallons;
    }

    public void setGallons(int gallons) {
        this.gallons = gallons;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
