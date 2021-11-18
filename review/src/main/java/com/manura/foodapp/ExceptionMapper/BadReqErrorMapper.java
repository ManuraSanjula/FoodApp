package com.manura.foodapp.ExceptionMapper;

import com.manura.foodapp.BadReqError;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BadReqErrorMapper implements ExceptionMapper<BadReqError> {
 
    @Override
    public Response toResponse(BadReqError exception) {
       return Response.status(Response.Status.BAD_REQUEST)
                .build();
    }
}
