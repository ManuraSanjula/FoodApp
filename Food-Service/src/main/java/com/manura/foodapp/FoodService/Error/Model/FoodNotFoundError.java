package com.manura.foodapp.FoodService.Error.Model;

public class FoodNotFoundError extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1923959438688743817L;

	public FoodNotFoundError(String message) {
        super(message);
    }
}
