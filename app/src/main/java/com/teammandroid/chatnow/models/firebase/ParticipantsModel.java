package com.teammandroid.chatnow.models.firebase;

public class ParticipantsModel {

    String role,timeStamp,uid;
    String clearDate;
    String token;

    public ParticipantsModel() {
    }

    public ParticipantsModel(String role, String timeStamp, String uid, String clearDate,String token) {
        this.role = role;
        this.timeStamp = timeStamp;
        this.uid = uid;
        this.clearDate = clearDate;
        this.token=token;
    }

    public ParticipantsModel(String role, String timeStamp, String uid, String clearDate) {
        this.role = role;
        this.timeStamp = timeStamp;
        this.uid = uid;
        this.clearDate = clearDate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getClearDate() {
        return clearDate;
    }

    public void setClearDate(String clearDate) {
        this.clearDate = clearDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "ParticipantsModel{" +
                "role='" + role + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", uid='" + uid + '\'' +
                ", clearDate='" + clearDate + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
