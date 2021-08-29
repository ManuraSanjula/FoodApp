package com.manura.foodapp.OrderService.Error.Model;

public class CartSerivceError extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2337826682204516191L;

	public CartSerivceError(String message) {
        super(message);
    }
}
