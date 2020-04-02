package com.example.rehabilitation.Data;

public class RecordValue {
    private String recDataID;
    private String recID;
    private String value;
    private double time;
    private String sTime;

    public String getsTime() {
        return sTime;
    }

    public void setsTime(String sTime) {
        this.sTime = sTime;
    }



    public RecordValue(String recDataID, String recID,String value, double time){
        this.recDataID=recDataID;
        this.recID=recID;
        this.value=value;
        this.time=time;
    }

    public RecordValue(String recDataID, String recID,String value, String time){
        this.recDataID=recDataID;
        this.recID=recID;
        this.value=value;
        this.sTime=time;
    }

    public String getRecDataID() {
        return recDataID;
    }

    public void setRecDataID(String recDataID) {
        this.recDataID = recDataID;
    }

    public String getRecID() {
        return recID;
    }

    public void setRecID(String recID) {
        this.recID = recID;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }



}
