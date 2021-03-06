package com.manura.foodapp.FoodService.Error.Controller;

import java.util.Date;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.manura.foodapp.FoodService.Error.Model.FoodError;
import com.manura.foodapp.FoodService.Error.Model.FoodNotFoundError;
import com.manura.foodapp.FoodService.Error.Model.Res.ErrorMessage;

import reactor.core.publisher.Mono;

@ControllerAdvice
public class ErrorController {

    @ExceptionHandler(FoodNotFoundError.class)
    public Mono<ResponseEntity<ErrorMessage>> handleFoodNotFoundError(FoodNotFoundError ex){
        
        ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage));
    }

    @ExceptionHandler(FoodError.class)
    public Mono<ResponseEntity<ErrorMessage>> handleFoodError(FoodError ex){
        
        ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage));
    }    
    
}
