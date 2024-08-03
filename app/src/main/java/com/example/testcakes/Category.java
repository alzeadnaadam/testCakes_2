package com.example.testcakes;

import java.util.Map;

// Category.java
public class Category {
    private String name;
    private int numberOfProducts;
    private Map<String, Product> products; // Using Map to represent products

    public Category() {
        // Default constructor required for calls to DataSnapshot.getValue(Category.class)
    }

    public Category(String name, int numberOfProducts, Map<String, Product> products) {
        this.name = name;
        this.numberOfProducts = numberOfProducts;
        this.products = products;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfProducts() {
        return numberOfProducts;
    }

    public void setNumberOfProducts(int numberOfProducts) {
        this.numberOfProducts = numberOfProducts;
    }

    public Map<String, Product> getProducts() {
        return products;
    }

    public void setProducts(Map<String, Product> products) {
        this.products = products;
    }
}
