package com.example.oderteaandroid;

import java.io.Serializable;

public class CartItem implements Serializable {
    private int id;
    private String name;
    private double price;
    private int quantity;
    private int imageResource;

    public CartItem(int id, String name, double price, int quantity, int imageResource) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageResource = imageResource;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
}
