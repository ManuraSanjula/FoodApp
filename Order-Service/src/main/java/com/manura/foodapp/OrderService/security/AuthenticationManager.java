package com.manura.foodapp.OrderService.security;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.manura.foodapp.OrderService.Service.Impl.OrderServiceImpl;
import com.manura.foodapp.OrderService.security.auth.UnauthorizedException;
import com.manura.foodapp.OrderService.security.auth.UserPrincipal;

import reactor.core.publisher.Mono;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {
    private final OrderServiceImpl service;

    public AuthenticationManager(OrderServiceImpl service) {
        this.service = service;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        var principal = (UserPrincipal) authentication.getPrincipal();
        return service.getUser(principal.getId())
                .filter(user -> user.getActive())
                .switchIfEmpty(Mono.error(new UnauthorizedException("User account is disabled.")))
                .map(user -> authentication);
    }
}
