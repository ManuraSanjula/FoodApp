package com.manura.foodapp.OrderService.Error.Model;

public class OrderSerivceError extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2337826682204516191L;

	public OrderSerivceError(String message) {
        super(message);
    }
}
