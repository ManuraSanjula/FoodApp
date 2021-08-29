package com.manura.foodapp.OrderService.Error.Model;

public class CartSerivceServerError extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1246018972414794001L;

	public CartSerivceServerError(String message) {
        super(message);
    }
}
