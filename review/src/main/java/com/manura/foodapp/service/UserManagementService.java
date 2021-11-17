/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.manura.foodapp.service;

import com.manura.foodapp.entity.UserEntity;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import javax.json.JsonValue;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;
import org.modelmapper.ModelMapper;

/**
 *
 * @author Manura Sanjula
 */
@Stateless
public class UserManagementService {

    @Inject
    @ConfigProperty(name = "userserviceurl")
    private String userserviceurl;
    private Client client;
    private WebTarget webTarget;
    private ModelMapper modelMapper = new ModelMapper();
    @Inject
    private ReviewService reviewService;

    @PostConstruct
    private void init() {
        System.out.println("com.manura.foodapp.service.UserManagementService.init() = " + userserviceurl);
        client = ClientBuilder.newBuilder().connectTimeout(7, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS).build();
        webTarget = client.target(userserviceurl);
    }

    @PreDestroy
    private void destroy() {
        if (client != null) {
            client.close();
        }

    }

    public UserEntity getUseDataFromUserService(String email, String token) {
        JsonValue jsonValue = webTarget.path("{email}")
                .resolveTemplate("email", email).request(MediaType.APPLICATION_JSON)
                .header("Authorization", token).get(JsonValue.class);
        JsonObject jsonObject = jsonValue.asJsonObject();
        UserEntity userDto = new UserEntity();
        String firstName = jsonObject.getString("firstName");
        String lastName = jsonObject.getString("lastName");
        String useremail = jsonObject.getString("email");
        String pic = jsonObject.getString("pic");

        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setEmail(useremail);

        userDto.setPic(pic);
        reviewService.saveUser(userDto);
        return userDto;
    }
}
