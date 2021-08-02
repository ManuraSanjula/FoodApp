package com.manura.foodservice.Error;

import java.util.Date;

import com.manura.foodservice.Error.Model.FoodError;
import com.manura.foodservice.Error.Model.FoodNotFoundError;
import com.manura.foodservice.Error.Res.ErrorMessage;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
