package com.manura.foodapp.FoodHutService.Error.Model;

public class FoodHutError extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2337826682204516191L;

	public FoodHutError(String message) {
        super(message);
    }
}
