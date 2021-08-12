package com.manura.foodapp.FoodHutService.Error.Controller;

import java.util.Date;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.manura.foodapp.FoodHutService.Error.Model.FoodHutError;
import com.manura.foodapp.FoodHutService.Error.Model.FoodHutNotFoundError;
import com.manura.foodapp.FoodHutService.Error.Model.Res.ErrorMessage;

import reactor.core.publisher.Mono;

@ControllerAdvice
public class ErrorController {

    @ExceptionHandler(FoodHutNotFoundError.class)
    public Mono<ResponseEntity<ErrorMessage>> handleFoodNotFoundError(FoodHutNotFoundError ex){
        
        ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage));
    }

    @ExceptionHandler(FoodHutError.class)
    public Mono<ResponseEntity<ErrorMessage>> handleFoodError(FoodHutError ex){
        
        ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage));
    }    
    
}
