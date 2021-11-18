package com.manura.foodapp;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionErrorMapper implements ExceptionMapper<Exception> {
 
    @Override
    public Response toResponse(Exception exception) {
       return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .build();
    }
}
