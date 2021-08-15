package com.manura.foodapp.FoodHutService.RouterFunctions;

import java.util.Date;
import java.util.function.BiFunction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.manura.foodapp.FoodHutService.Error.Model.FoodHutError;
import com.manura.foodapp.FoodHutService.Error.Model.FoodHutNotFoundError;
import com.manura.foodapp.FoodHutService.Error.Model.Res.ErrorMessage;

import reactor.core.publisher.Mono;

@Configuration
public class RouterConfig {
	
	@Autowired
    private RequestHandler requestHandler;

    @Bean
    public RouterFunction<ServerResponse> highLevelRouter(){
        return RouterFunctions.route()
                .path("foodHuts", this::serverResponseRouterFunction)
                .build();
    }
    private RouterFunction<ServerResponse> serverResponseRouterFunction(){
        return RouterFunctions.route()
                .GET("", requestHandler::getAllFoodHuts)
                .GET("/{id}", requestHandler::getOneFoodHut)
                .PUT("/{id}", requestHandler::updateFoodHut)
                .POST("", requestHandler::saveFooHut)
                .POST("/{id}/comment", requestHandler::saveComment)
                .onError(FoodHutError.class, foodHutErrorexceptionHandler())
                .onError(FoodHutNotFoundError.class, foodHutNotFoundErrorexceptionHandler())
                .build();
    }
    private BiFunction<Throwable, ServerRequest, Mono<ServerResponse>> foodHutErrorexceptionHandler(){
        return (err, req) -> {
        	FoodHutError ex = (FoodHutError) err;
        	ErrorMessage response = new ErrorMessage();
            response.setMessage(ex.getMessage());
            response.setTimestamp(new Date());
            return ServerResponse.badRequest().bodyValue(response);
        };
    }
    private BiFunction<Throwable, ServerRequest, Mono<ServerResponse>> foodHutNotFoundErrorexceptionHandler(){
        return (err, req) -> {
        	FoodHutNotFoundError ex = (FoodHutNotFoundError) err;
        	ErrorMessage response = new ErrorMessage();
            response.setMessage(ex.getMessage());
            response.setTimestamp(new Date());
            return ServerResponse.status(404).bodyValue(response);
        };
    }
}