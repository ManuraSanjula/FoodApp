package com.manura.foodapp.OrderService.Error.Model;

public class OrderSerivceNotFoundError extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1923959438688743817L;

	public OrderSerivceNotFoundError(String message) {
        super(message);
    }
}
