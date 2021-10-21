package com.manura.foodapp.OrderService.security.support;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ServerWebExchange;
import com.manura.foodapp.OrderService.Utils.TokenConverter;
import com.manura.foodapp.OrderService.security.auth.UserPrincipal;

import reactor.core.publisher.Mono;

public class ServerHttpBearerAuthenticationConverter implements Function<ServerWebExchange, Mono<Authentication>> {

	private static final String BEARER = "Bearer ";
	private static final Predicate<String> matchBearerLength = authValue -> authValue.length() > BEARER.length();
	private static final Function<String, Mono<String>> isolateBearerValue = authValue -> Mono
			.justOrEmpty(authValue.substring(BEARER.length()));
	
	private final TokenConverter tokenConverter;

	public ServerHttpBearerAuthenticationConverter(TokenConverter tokenConverter) {
		super();
		this.tokenConverter = tokenConverter;
	}

	@Override
	public Mono<Authentication> apply(ServerWebExchange serverWebExchange) {
		return Mono.justOrEmpty(serverWebExchange).flatMap(ServerHttpBearerAuthenticationConverter::extractToken)
				.filter(matchBearerLength).flatMap(isolateBearerValue).flatMap(user -> {
					List<GrantedAuthority> authorities = new ArrayList<>();
					return tokenConverter.validateTokenSignature(user).map(u -> {
						UserPrincipal principal = new UserPrincipal(u.getPublicId(), u.getEmail());
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

	public static Mono<String> extractToken(ServerWebExchange serverWebExchange) {
		 ServerHttpRequest request = serverWebExchange.getRequest();

		String tokenFromBody = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
	    String authJwt = request.getQueryParams().getFirst("token");
	    if(tokenFromBody == null) {
			return Mono.justOrEmpty(authJwt);
	    }else {
			return Mono.justOrEmpty(tokenFromBody);
	    }
	}
}








