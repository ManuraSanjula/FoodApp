package com.manura.foodapp.UserService.security;

import com.manura.foodapp.UserService.Service.impl.UserServiceImpl;
import com.manura.foodapp.UserService.shared.Utils.JWT.security.token.creator.TokenCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final UserServiceImpl userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenCreator tokenCreator;
    
    @Value("${app.public.routes}")
    private String [] publicRoutes;
    
    @Value("${app.user.roles}")
    private String [] roles;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	http.headers().httpStrictTransportSecurity().disable();
        http
                .cors().and()
                .csrf().disable().authorizeRequests()
                .antMatchers(publicRoutes).permitAll()
                .antMatchers("/users/{email}").hasAnyAuthority(roles)
                 .antMatchers("/users/signup/Admin").hasAnyAuthority("ROLE_ADMIN")
                .anyRequest().authenticated().and()
                .addFilter(getAuthenticationFilter())
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .exceptionHandling().authenticationEntryPoint((req, resp, e) -> resp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .accessDeniedHandler((req,resp,e) -> resp.sendError(HttpServletResponse.SC_FORBIDDEN));

        http.headers().frameOptions().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    protected AuthenticationFilter getAuthenticationFilter() throws Exception {
        final AuthenticationFilter filter = new AuthenticationFilter(authenticationManager(), tokenCreator);
        filter.setFilterProcessesUrl("/users/login");
        return filter;
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
}
