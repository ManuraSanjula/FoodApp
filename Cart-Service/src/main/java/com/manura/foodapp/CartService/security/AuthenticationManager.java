package com.manura.foodapp.CartService.security;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.manura.foodapp.CartService.Service.Impl.CartServiceImpl;
import com.manura.foodapp.CartService.security.auth.UnauthorizedException;
import com.manura.foodapp.CartService.security.auth.UserPrincipal;

import reactor.core.publisher.Mono;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {
    private final CartServiceImpl userService;

    public AuthenticationManager(CartServiceImpl userService) {
        this.userService = userService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
    	UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return userService.getUser(principal.getId())
                .filter(user -> user.getActive())
                .switchIfEmpty(Mono.error(new UnauthorizedException("User account is disabled.")))
                .map(user -> authentication);
    }
}
