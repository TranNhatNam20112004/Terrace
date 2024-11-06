package com.example.terrace.model;

public class User {

   private String account, pass, mail, role,phone, userId;

    public User() {
    }

    public User(String account, String pass, String mail, String role, String phone) {
        this.account = account;
        this.pass = pass;
        this.mail = mail;
        this.role = role;
        this.phone = phone;
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String name) {
        this.mail = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
