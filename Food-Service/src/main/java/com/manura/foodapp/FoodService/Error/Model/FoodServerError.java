package com.manura.foodapp.FoodService.Error.Model;

public class FoodServerError extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1246018972414794001L;

	public FoodServerError(String message) {
        super(message);
    }
}
