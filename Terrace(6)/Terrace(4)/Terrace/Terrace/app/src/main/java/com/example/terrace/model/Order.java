package com.example.terrace.model;

import com.google.firebase.Timestamp;

public class Order {
    private String orderId;
    private String userId;
    private String username;
    private String address;
    private Timestamp orderdate;
    private String paymethod;
    private String phone;
    private String status;
    private float totalAmount;

    public Order() {} // Constructor mặc định là cần thiết cho Firestore

    public Order(String orderId, String userId, String username, String address, Timestamp orderDate, String paymethod, String phone, String status, float totalAmount) {
        this.orderId = orderId;
        this.userId = userId;
        this.username = username;
        this.address = address;
        this.orderdate = orderDate;
        this.paymethod = paymethod;
        this.phone = phone;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Timestamp getOrderDate() {
        return orderdate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderdate = orderDate;
    }

    public String getPaymethod() {
        return paymethod;
    }

    public void setPaymethod(String paymethod) {
        this.paymethod = paymethod;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }
}
