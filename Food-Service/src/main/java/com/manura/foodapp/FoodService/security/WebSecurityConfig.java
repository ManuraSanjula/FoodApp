package com.manura.foodapp.FoodService.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import com.manura.foodapp.FoodService.security.support.ServerHttpBearerAuthenticationConverter;

import reactor.core.publisher.Mono;

@Configuration
@EnableReactiveMethodSecurity
public class WebSecurityConfig {
	private final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

//	@Value("${jwt.secret}")
//	private String jwtSecret;
//
//	@Value("${app.public_routes}")
//	private String[] publicRoutes;

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, AuthenticationManager authManager) {
		return http.authorizeExchange().pathMatchers(HttpMethod.OPTIONS).permitAll()
				//.pathMatchers(publicRoutes).permitAll()
				.pathMatchers("/favicon.ico").permitAll().anyExchange().authenticated().and().csrf()
				.disable().httpBasic().disable().formLogin().disable().exceptionHandling()
				.authenticationEntryPoint((swe, e) -> {
					logger.info("[1] Authentication error: Unauthorized[401]: " + e.getMessage());

					return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
				}).accessDeniedHandler((swe, e) -> {
					logger.info("[2] Authentication error: Access Denied[401]: " + e.getMessage());

					return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
				}).and().addFilterAt(bearerAuthenticationFilter(authManager), SecurityWebFiltersOrder.AUTHENTICATION)
//				.addFilterAt(cookieAuthenticationFilter(authManager), SecurityWebFiltersOrder.AUTHENTICATION)
				.build();
	}

	AuthenticationWebFilter bearerAuthenticationFilter(AuthenticationManager authManager) {
		AuthenticationWebFilter bearerAuthenticationFilter = new AuthenticationWebFilter(authManager);
		bearerAuthenticationFilter.setAuthenticationConverter(new ServerHttpBearerAuthenticationConverter());
		bearerAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));

		return bearerAuthenticationFilter;
	}
}
