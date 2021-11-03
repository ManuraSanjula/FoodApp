package com.manura.foodapp.OrderService.security;

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
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.manura.foodapp.OrderService.Utils.TokenConverter;
import com.manura.foodapp.OrderService.security.support.ServerHttpBearerAuthenticationConverter;

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
				.pathMatchers("/orders/pdf/{orderId}").permitAll().pathMatchers("/orders/order-confrim-web").permitAll()
				.pathMatchers("/orders/order-completed/{deliveryGuyEmail}/{email}/{orderId}")
				.hasAnyAuthority("ROLE_DELIVERY")
				.pathMatchers("/orders/order-accepted/{deliveryGuyEmail}/{email}/{orderId}")
				.hasAnyAuthority("ROLE_DELIVERY").pathMatchers("/orders/**").hasAnyAuthority("ROLE_USER").and().csrf()
				.disable().httpBasic().disable().formLogin().disable().exceptionHandling()
				.authenticationEntryPoint((swe, e) -> {
					logger.info("[1] Authentication error: Unauthorized[401]: " + e.getMessage());

					return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
				}).accessDeniedHandler((swe, e) -> {
					logger.info("[2] Authentication error: Access Denied[401]: " + e.getMessage());

					return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
				}).and().addFilterAt(bearerAuthenticationFilter(authManager), SecurityWebFiltersOrder.AUTHENTICATION)
				.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowCredentials(false);
		configuration.setAllowedHeaders(Arrays.asList("*"));

		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}

	AuthenticationWebFilter bearerAuthenticationFilter(AuthenticationManager authManager) {
		AuthenticationWebFilter bearerAuthenticationFilter = new AuthenticationWebFilter(authManager);
		bearerAuthenticationFilter
				.setAuthenticationConverter(new ServerHttpBearerAuthenticationConverter(tokenConverter));
		bearerAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));

		return bearerAuthenticationFilter;
	}
}
