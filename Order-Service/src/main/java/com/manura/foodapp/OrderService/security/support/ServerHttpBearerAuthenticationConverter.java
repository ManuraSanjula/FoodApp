package com.manura.foodapp.OrderService.security.support;

import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;

import com.manura.foodapp.OrderService.Utils.TokenConverter;
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
					return tokenConverter.validateTokenSignature(user).map(u -> {
						return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(u, null, 
								u.getAuthorities()));
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








