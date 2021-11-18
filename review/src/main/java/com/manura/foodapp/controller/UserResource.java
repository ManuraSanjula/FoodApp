package com.manura.foodapp.controller;

import com.manura.foodapp.Secure;
import com.manura.foodapp.entity.ReviewEntity;
import com.manura.foodapp.service.ReviewService;
import java.util.List;
import javax.ws.rs.*;
import javax.inject.Inject;

@Path("")
public class UserResource {

    @Inject
    ReviewService managementService;

    @Secure
    @POST
    @Path("/{user}")
    public ReviewEntity saveUser(@PathParam("user") String user, @HeaderParam("Authorization") String token, @QueryParam("comment") String comment) {
        ReviewEntity saveComment = managementService.saveComment(comment, user, token);
        return saveComment;
    }

    @GET
    @Path("/all")
    public List<ReviewEntity> getAllComments() {
        return managementService.getAllComments();
    }

    @GET
    public String user() {
        return "HI Welcome Review Service";
    }

}
