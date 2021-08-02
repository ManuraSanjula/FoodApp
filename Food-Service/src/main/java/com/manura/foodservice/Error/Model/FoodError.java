package com.manura.foodservice.Error.Model;

public class FoodError extends RuntimeException {
    public FoodError(String message) {
        super(message);
    }
}
