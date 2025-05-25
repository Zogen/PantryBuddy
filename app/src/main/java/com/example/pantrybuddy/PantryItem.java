package com.example.pantrybuddy;

public class PantryItem {
    private int id;
    private String name;
    private int quantity;
    private String expirationDate;

    public PantryItem(int id, String name, int quantity, String expirationDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getExpirationDate() { return expirationDate; }

    public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }

}
