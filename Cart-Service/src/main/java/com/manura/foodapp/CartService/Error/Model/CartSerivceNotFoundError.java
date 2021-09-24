package com.manura.foodapp.CartService.Error.Model;

public class CartSerivceNotFoundError extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1923959438688743817L;

	public CartSerivceNotFoundError(String message) {
        super(message);
    }
}
