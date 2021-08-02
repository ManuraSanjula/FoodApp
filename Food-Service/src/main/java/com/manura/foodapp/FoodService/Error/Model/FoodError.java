package com.manura.foodapp.FoodService.Error.Model;

public class FoodError extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2337826682204516191L;

	public FoodError(String message) {
        super(message);
    }
}
