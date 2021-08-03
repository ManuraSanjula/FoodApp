package com.manura.foodapp.FoodService.security.support;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ServerWebExchange;
import com.manura.foodapp.FoodService.security.auth.UserPrincipal;
import com.manura.foodapp.FoodService.util.TokenConverter;

import reactor.core.publisher.Mono;

public class ServerHttpBearerAuthenticationConverter implements Function<ServerWebExchange, Mono<Authentication>> {

	private static final String BEARER = "Bearer ";
	private static final Predicate<String> matchBearerLength = authValue -> authValue.length() > BEARER.length();
	private static final Function<String, Mono<String>> isolateBearerValue = authValue -> Mono
			.justOrEmpty(authValue.substring(BEARER.length()));
	@Autowired
	private TokenConverter tokenConverter;

	@Override
	public Mono<Authentication> apply(ServerWebExchange serverWebExchange) {
		return Mono.justOrEmpty(serverWebExchange).flatMap(ServerHttpBearerAuthenticationConverter::extract)
				.filter(matchBearerLength).flatMap(isolateBearerValue).flatMap(user -> {
					List<GrantedAuthority> authorities = new ArrayList<>();
					return tokenConverter.validateTokenSignature(user).map(u -> {
						var principal = new UserPrincipal(u.getId(), u.getEmail());
						u.getRoles().forEach(role -> {
							authorities.add(new SimpleGrantedAuthority(role));
						});
						u.getAuthorities().forEach(aut -> {
							authorities.add(new SimpleGrantedAuthority(aut));
						});
						return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(principal, null, authorities));
					}).flatMap(i -> i);
				});
	}

	public static Mono<String> extract(ServerWebExchange serverWebExchange) {
		return Mono.justOrEmpty(serverWebExchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
	}
}
