package com.example.testcakes;

public class Order {
    private double cost;
    private String date;
    private String phoneNumber;

    public Order() {
        // Default constructor required for calls to DataSnapshot.getValue(Order.class)
    }

    public Order(double cost, String date, String phoneNumber) {
        this.cost = cost;
        this.date = date;
        this.phoneNumber = phoneNumber;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

