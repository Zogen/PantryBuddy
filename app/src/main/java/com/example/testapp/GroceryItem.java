package com.example.testapp;

public class GroceryItem {
    private int id;
    private String name;
    private int quantity;

    public GroceryItem(int id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }
}
