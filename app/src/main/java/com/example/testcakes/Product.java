package com.example.testcakes;
public class Product {
    private String productName; // Matches "productName" in Firebase
    private double price; // Matches "price" in Firebase

    // Default constructor (required for Firebase)
    public Product() {
    }

    // Getters and Setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

