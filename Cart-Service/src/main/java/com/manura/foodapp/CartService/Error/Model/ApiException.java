package com.manura.foodapp.CartService.Error.Model;

import lombok.Getter;

public class ApiException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8409271787665076669L;
	@Getter
    protected String errorCode;

    public ApiException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
