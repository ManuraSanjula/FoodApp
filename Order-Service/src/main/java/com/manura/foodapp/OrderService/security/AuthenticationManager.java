package com.manura.foodapp.OrderService.security;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.manura.foodapp.OrderService.Service.Impl.OrderServiceImpl;
import com.manura.foodapp.OrderService.Table.UserTable;
import com.manura.foodapp.OrderService.security.auth.UnauthorizedException;

import reactor.core.publisher.Mono;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {
    private final OrderServiceImpl service;

    public AuthenticationManager(OrderServiceImpl service) {
        this.service = service;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
    	UserTable principal =  (UserTable) authentication.getPrincipal();
        return service.getUser(principal.getPublicId())
                .filter(user -> user.getActive() && user.getEmailVerify() && user.getAccountNonExpired()
                		&& user.getAccountNonLocked())
                .switchIfEmpty(Mono.error(new UnauthorizedException("User account is disabled.")))
                .map(user -> authentication);
    }
}
