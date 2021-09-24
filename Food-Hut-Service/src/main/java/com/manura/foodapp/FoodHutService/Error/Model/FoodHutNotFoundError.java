package com.manura.foodapp.FoodHutService.Error.Model;

public class FoodHutNotFoundError extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1923959438688743817L;

	public FoodHutNotFoundError(String message) {
        super(message);
    }
}
