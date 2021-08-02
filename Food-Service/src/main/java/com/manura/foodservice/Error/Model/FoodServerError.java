package com.manura.foodservice.Error.Model;

public class FoodServerError extends RuntimeException {
    public FoodServerError(String message) {
        super(message);
    }
}
