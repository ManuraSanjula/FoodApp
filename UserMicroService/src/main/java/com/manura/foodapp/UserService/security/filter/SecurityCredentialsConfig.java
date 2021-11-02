package com.manura.foodapp.UserService.security.filter;

import com.manura.foodapp.UserService.Service.impl.UserServiceImpl;
import com.manura.foodapp.UserService.security.WebSecurity;
import com.manura.foodapp.UserService.security.google.Google2faFilter;
import com.manura.foodapp.UserService.shared.Utils.JWT.security.property.JwtConfiguration;
import com.manura.foodapp.UserService.shared.Utils.JWT.security.token.converter.TokenConverter;
import com.manura.foodapp.UserService.shared.Utils.JWT.security.token.creator.TokenCreator;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled=true, prePostEnabled=true)
public class SecurityCredentialsConfig extends WebSecurity {
    
    private final TokenConverter tokenConverter;
    private final JwtConfiguration jwtConfiguration;

    public SecurityCredentialsConfig(Google2faFilter google2faFilter,JwtConfiguration jwtConfiguration, TokenConverter tokenConverter, UserServiceImpl userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder, TokenCreator tokenCreator) {
        super(userDetailsService,bCryptPasswordEncoder,tokenCreator, google2faFilter);
        this.tokenConverter = tokenConverter;
        this.jwtConfiguration = jwtConfiguration;
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterAfter(new JwtTokenAuthorizationFilter(jwtConfiguration, tokenConverter), UsernamePasswordAuthenticationFilter.class);
        super.configure(http);
    }

}
