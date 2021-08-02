package com.manura.foodservice.Error.Model;

public class FoodNotFoundError extends RuntimeException {
    public FoodNotFoundError(String message) {
        super(message);
    }
}
