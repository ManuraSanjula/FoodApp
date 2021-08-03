package com.manura.foodapp.FoodService.security;


import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.manura.foodapp.FoodService.security.auth.UnauthorizedException;
import com.manura.foodapp.FoodService.security.auth.UserPrincipal;
import com.manura.foodapp.FoodService.service.impl.FoodServiceImpl;

import reactor.core.publisher.Mono;


@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {
    private final FoodServiceImpl userService;

    public AuthenticationManager(FoodServiceImpl userService) {
        this.userService = userService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        var principal = (UserPrincipal) authentication.getPrincipal();
        return userService.getUser(principal.getId())
                .filter(user -> user.getActive())
                .switchIfEmpty(Mono.error(new UnauthorizedException("User account is disabled.")))
                .map(user -> authentication);
    }
}
