package com.example.terrace.model;

public class OrderDetail {
    private String id;
    private String drinkId;
    private String dname;
    private String orderId;
    private int quantity;
    private int price;

    public OrderDetail() {}

    public OrderDetail(String id, String drinkId, String dname, String orderId, int quantity, int price) {
        this.id = id;
        this.drinkId = drinkId;
        this.dname = dname;
        this.orderId = orderId;
        this.quantity = quantity;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDrinkId() {
        return drinkId;
    }

    public void setDrinkId(String drinkId) {
        this.drinkId = drinkId;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
    public int getTotal(){
        return quantity * price;
    }
}
