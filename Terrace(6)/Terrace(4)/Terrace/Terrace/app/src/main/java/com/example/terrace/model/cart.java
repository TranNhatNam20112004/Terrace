package com.example.terrace.model;

public class cart {
    private String name, image,user, id;
    private float price, quantity;

    public cart() {
    }


    public cart(String name, String image, String user, float price, float quantity) {
        this.name = name;
        this.image = image;
        this.user = user;
        this.price = price;
        this.quantity = quantity;
    }

    public cart(String name,String id, String image, String user, float price, float quantity) {
        this.name = name;
        this.image = image;
        this.user = user;
        this.id = id;
        this.price = price;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }
}
