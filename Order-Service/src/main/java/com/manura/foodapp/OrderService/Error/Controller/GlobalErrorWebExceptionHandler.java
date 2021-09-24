package com.manura.foodapp.OrderService.Error.Controller;

import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;
import java.util.Map;

@SuppressWarnings("deprecation")
@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {


    public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes,
                                              ApplicationContext applicationContext,
                                              ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, new ResourceProperties(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(
            ErrorAttributes errorAttributes) {

        return RouterFunctions.route(
                RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(
            ServerRequest request) {

        Map<String, Object> errorPropertiesMap = getErrorAttributes(request,
                ErrorAttributeOptions.defaults());


        if(errorPropertiesMap.get("error") == "Not Found"){
            return ServerResponse.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(errorPropertiesMap));
        }else if(errorPropertiesMap.get("error") == "Unauthorized"){
        	return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(""));
        }
        else {
        	 return ServerResponse.status(HttpStatus.BAD_REQUEST)
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(errorPropertiesMap));
        }
    }

}