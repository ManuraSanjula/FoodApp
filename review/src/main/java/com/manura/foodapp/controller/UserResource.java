package com.manura.foodapp.controller;

import com.manura.foodapp.dto.UserDto;
import com.manura.foodapp.service.UserManagementService;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author airhacks.com
 */
@Path("")
@Consumes("application/json")
@Produces("application/json")
@RequestScoped
public class UserResource {

    @Inject
    @ConfigProperty(name = "message")
    String message;    
    
    @Inject
    UserManagementService managementService;

    @GET
    public String ping() {
        return this.message + " Jakarta EE with MicroProfile 2+!";
    }
    
    @GET
    @Path("{user}")
    public String user(String user, @HeaderParam("Authorization") String token) {
        return managementService.getUseDataFromUserService(user,token);
    }

}
