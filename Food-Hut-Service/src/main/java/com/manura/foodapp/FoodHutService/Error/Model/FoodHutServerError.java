package com.manura.foodapp.FoodHutService.Error.Model;

public class FoodHutServerError extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1246018972414794001L;

	public FoodHutServerError(String message) {
        super(message);
    }
}
