package com.manura.foodapp.OrderService.Error.Controller;

import java.util.Date;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.manura.foodapp.OrderService.Error.Model.CartSerivceError;
import com.manura.foodapp.OrderService.Error.Model.CartSerivceNotFoundError;
import com.manura.foodapp.OrderService.Error.Model.Res.ErrorMessage;

import reactor.core.publisher.Mono;

@ControllerAdvice
public class ErrorController {

    @ExceptionHandler(CartSerivceNotFoundError.class)
    public Mono<ResponseEntity<ErrorMessage>> handleFoodNotFoundError(CartSerivceNotFoundError ex){
        
        ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage));
    }

    @ExceptionHandler(CartSerivceError.class)
    public Mono<ResponseEntity<ErrorMessage>> handleFoodError(CartSerivceError ex){
        
        ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage));
    }    
    
}
