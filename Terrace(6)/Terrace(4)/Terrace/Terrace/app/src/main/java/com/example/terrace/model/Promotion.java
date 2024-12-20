package com.example.terrace.model;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Promotion {
    private String id; // Thêm thuộc tính ID
    private String name;
    private int discount;
    private Timestamp start;
    private Timestamp end;
    private int quantity;

    public Promotion() {
        // Constructor mặc định
    }

    public Promotion(String name, int discount, Timestamp start, Timestamp end, int quantity) {
        this.name = name;
        this.discount = discount;
        this.start = start;
        this.end = end;
        this.quantity = quantity;
    }

    public Promotion(String id, String name, int discount, Timestamp start, Timestamp end, int quantity) {
        this.id = id;
        this.name = name;
        this.discount = discount;
        this.start = start;
        this.end = end;
        this.quantity = quantity;
    }

    // Getters và setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}