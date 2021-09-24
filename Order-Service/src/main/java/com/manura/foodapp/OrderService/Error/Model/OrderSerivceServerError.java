package com.manura.foodapp.OrderService.Error.Model;

public class OrderSerivceServerError extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1246018972414794001L;

	public OrderSerivceServerError(String message) {
        super(message);
    }
}
