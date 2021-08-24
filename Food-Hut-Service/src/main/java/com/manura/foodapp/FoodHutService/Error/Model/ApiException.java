package com.manura.foodapp.FoodHutService.Error.Model;

import lombok.Getter;

public class ApiException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6962160626046228297L;
	@Getter
    protected String errorCode;

    public ApiException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
