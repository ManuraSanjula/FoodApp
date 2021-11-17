package com.manura.foodapp.controller;

import com.manura.foodapp.entity.UserEntity;
import com.manura.foodapp.service.UserManagementService;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.inject.Inject;

@Path("")
@RequestScoped
public class UserResource { 
   @Inject
    UserManagementService managementService;
    @GET
    @Path("/{user}")
    public UserEntity user(@PathParam("user") String user, @HeaderParam("Authorization") String token) {
        UserEntity useDataFromUserService = managementService.getUseDataFromUserService(user, token);
        return useDataFromUserService;
    }

}
