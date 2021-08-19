package com.manura.foodapp.UserService.Ui.controller.error;

import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping({UserErrorController.ERROR_PATH})
public class UserErrorController extends AbstractErrorController {

    static final String ERROR_PATH = "/error";

    ErrorAttributes errorAttributes;

    public UserErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes, Collections.emptyList());
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping
    public ResponseEntity errorMapping(HttpServletRequest request){
        Map<String, Object> body = this.getErrorAttributes(request, false);
        HttpStatus status = this.getStatus(request);
        body.remove("message");
        body.remove("requestId");
        if(body.get("error").equals("Forbidden") && status.value() == Integer.valueOf((Integer) body.get("status"))){//Not Found
        	 return new ResponseEntity<>("", status);
        }
        if(body.get("error").equals("Unauthorized") && status.value() == Integer.valueOf((Integer) body.get("status"))){//Not Found
       	 return new ResponseEntity<>("", status);
       }
        if(body.get("error") == "Not Found" && status.value() == Integer.valueOf((Integer) body.get("status"))){
            body.replace("message","Please Enter Valid Url");
        }
        return new ResponseEntity<>(body, status);
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}
