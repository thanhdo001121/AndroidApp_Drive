package com.example.firebase.help;

public class MessHelp {
    String emailId,phoneNum,mess,id;


    public MessHelp() {
        this.emailId = "";
        this.phoneNum = "";
        this.mess = "";
        this.id = id;
    }

    public MessHelp(String emailId, String phoneNum, String mess) {
        this.emailId = emailId;
        this.phoneNum = phoneNum;
        this.mess = mess;
    }
    public MessHelp(String emailId, String phoneNum, String mess, String id) {
        this.emailId = emailId;
        this.phoneNum = phoneNum;
        this.mess = mess;
        this.id = id;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getMess() {
        return mess;
    }

    public String getId() {
        return id;
    }
}
