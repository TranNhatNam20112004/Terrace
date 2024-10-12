package com.example.terrace.model;

public class Branch {
    private String id;
    private String address;

    public Branch() {
        // Firebase cần một constructor trống
    }

    public Branch(String id, String address) {
        this.id = id;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}