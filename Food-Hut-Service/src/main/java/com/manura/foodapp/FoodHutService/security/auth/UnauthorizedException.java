package com.manura.foodapp.FoodHutService.security.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.manura.foodapp.FoodHutService.Error.Model.ApiException;



@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends ApiException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8680203960947950041L;

	public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED");
    }
}
