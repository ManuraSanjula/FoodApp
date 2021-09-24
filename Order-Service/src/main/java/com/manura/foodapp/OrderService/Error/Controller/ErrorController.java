package com.manura.foodapp.OrderService.Error.Controller;

import java.util.Date;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.manura.foodapp.OrderService.Error.Model.OrderSerivceError;
import com.manura.foodapp.OrderService.Error.Model.OrderSerivceNotFoundError;
import com.manura.foodapp.OrderService.Error.Model.Res.ErrorMessage;

import reactor.core.publisher.Mono;

@ControllerAdvice
public class ErrorController {

    @ExceptionHandler(OrderSerivceNotFoundError.class)
    public Mono<ResponseEntity<ErrorMessage>> handleFoodNotFoundError(OrderSerivceNotFoundError ex){
        
        ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage));
    }

    @ExceptionHandler(OrderSerivceError.class)
    public Mono<ResponseEntity<ErrorMessage>> handleFoodError(OrderSerivceError ex){
        
        ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage));
    }    
    
}
