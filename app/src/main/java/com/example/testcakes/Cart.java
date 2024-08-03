package com.example.testcakes;


public class Cart {

    private String id;
    private String productName;
    private String date;
    private double cost;
    private String phoneNumber;
    private int quantity;

    public Cart() {
        // Default constructor required for calls to DataSnapshot.getValue(Order.class)
    }

    public Cart(String productName, String date, double cost, String phoneNumber, int quantity) {
        this.productName = productName;
        this.date = date;
        this.cost = cost;
        this.phoneNumber = phoneNumber;
        this.quantity = quantity; // Initialize the quantity field
    }

    // Getters and setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
