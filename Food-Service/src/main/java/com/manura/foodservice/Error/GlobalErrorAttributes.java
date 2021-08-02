package com.manura.foodservice.Error;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request,
                                                  ErrorAttributeOptions options) {
        Map<String, Object> map = super.getErrorAttributes(
                request, options);
        if(map.get("error") == "Not Found"){
            map.put("status", HttpStatus.NOT_FOUND);
        }else map.put("status", HttpStatus.BAD_REQUEST);

        map.remove("message");
//        map.put("message", "username i;s required");
        return map;
    }

}
