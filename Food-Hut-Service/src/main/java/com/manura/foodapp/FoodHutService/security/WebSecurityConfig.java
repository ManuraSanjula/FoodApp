package com.manura.foodapp.FoodHutService.security;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.manura.foodapp.FoodHutService.security.support.ServerHttpBearerAuthenticationConverter;
import com.manura.foodapp.FoodHutService.utils.TokenConverter;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Configuration
@EnableReactiveMethodSecurity
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebSecurityConfig {
	private final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);
	private final TokenConverter tokenConverter;

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, AuthenticationManager authManager) {
		return http.authorizeExchange().pathMatchers(HttpMethod.OPTIONS).permitAll()
				.pathMatchers(HttpMethod.GET, "/foodHuts/**").permitAll().pathMatchers(HttpMethod.POST, "/foodHuts")
				.hasAnyAuthority("ROLE_ADMIN").pathMatchers(HttpMethod.PUT, "/foodHuts/{id}")
				.hasAnyAuthority("ROLE_ADMIN").pathMatchers(HttpMethod.PUT, "/foodHuts/{id}/coverImage")
				.hasAnyAuthority("ROLE_ADMIN").pathMatchers(HttpMethod.PUT, "/foodHuts/{id}/Images")
				.hasAnyAuthority("ROLE_ADMIN").pathMatchers(HttpMethod.POST, "/foodHuts/{id}/comments")
				.hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
				.pathMatchers(HttpMethod.DELETE, "/foodHuts/{foodHutId}/comments/{commentId}")
				.hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
				.pathMatchers(HttpMethod.PUT, "/foodHuts/{foodHutId}/comments/{commentId}")
				.hasAnyAuthority("ROLE_USER", "ROLE_ADMIN").and().csrf().disable().httpBasic().disable().formLogin()
				.disable().exceptionHandling().authenticationEntryPoint((swe, e) -> {
					logger.info("[1] Authentication error: Unauthorized[401]: " + e.getMessage());

					return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
				}).accessDeniedHandler((swe, e) -> {
					logger.info("[2] Authentication error: Access Denied[401]: " + e.getMessage());

					return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
				}).and().addFilterAt(bearerAuthenticationFilter(authManager), SecurityWebFiltersOrder.AUTHENTICATION)
				.build();
	}
	@Bean
	CorsWebFilter corsWebFilter() {
	    CorsConfiguration corsConfig = new CorsConfiguration();
	    corsConfig.setAllowedOrigins(Arrays.asList("*"));
	    corsConfig.setMaxAge(8000L);
	    corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	    corsConfig.setAllowedHeaders(Arrays.asList("*"));
        corsConfig.setAllowCredentials(false);
	    UrlBasedCorsConfigurationSource source =
	      new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", corsConfig);
	    return new CorsWebFilter(source);
	}
	AuthenticationWebFilter bearerAuthenticationFilter(AuthenticationManager authManager) {
		AuthenticationWebFilter bearerAuthenticationFilter = new AuthenticationWebFilter(authManager);
		bearerAuthenticationFilter
				.setAuthenticationConverter(new ServerHttpBearerAuthenticationConverter(tokenConverter));
		bearerAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));

		return bearerAuthenticationFilter;
	}
}
