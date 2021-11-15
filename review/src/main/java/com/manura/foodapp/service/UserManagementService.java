/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.manura.foodapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.CompletionStage;
import javax.ws.rs.core.Response;
import com.manura.foodapp.dto.UserDto;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import javax.json.JsonValue;

/**
 *
 * @author Manura Sanjula
 */
@RequestScoped
public class UserManagementService {

    @Inject
    @ConfigProperty(name = "userserviceurl")
    private String userserviceurl;
    private ObjectMapper objectMapper = new ObjectMapper();
    private Client client;
    private WebTarget webTarget;
    private UserDto userDto = new UserDto();
    private String user = "Hi";
    @PostConstruct
    private void init() {
        System.out.println("com.manura.foodapp.service.UserManagementService.init() = " + userserviceurl);
        client = ClientBuilder.newBuilder().connectTimeout(7, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS).build();
        webTarget = client.target(userserviceurl);
    }

    @PreDestroy
    private void destroy() {
        if (client != null) {
            client.close();
        }

    }

    public String getUseDataFromUserService(String email,String token) {
        CompletionStage<Response> responseCompletionStage = webTarget.path("{email}")
                .resolveTemplate("email", email).request()
                .header("Authorization", token).rx().get();
        responseCompletionStage.thenApply(response -> response.readEntity(JsonValue.class)).thenAccept(this::action);
        return user;
    }

    private void action(JsonValue json) {
        try {
            System.out.println("com.manura.foodapp.service.UserManagementService.action() = " + json.toString());
            user = json.toString();
            //userDto = objectMapper.readValue(userJson, UserDto.class);
        } catch (Exception e) {

        }
    }

}
