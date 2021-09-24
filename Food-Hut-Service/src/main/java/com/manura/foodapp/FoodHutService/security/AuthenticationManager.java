package com.manura.foodapp.FoodHutService.security;


import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.manura.foodapp.FoodHutService.Service.Impl.FoodHutServiceImpl;
import com.manura.foodapp.FoodHutService.security.auth.UnauthorizedException;
import com.manura.foodapp.FoodHutService.security.auth.UserPrincipal;

import reactor.core.publisher.Mono;


@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {
    private final FoodHutServiceImpl userService;

    public AuthenticationManager(FoodHutServiceImpl userService) {
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
