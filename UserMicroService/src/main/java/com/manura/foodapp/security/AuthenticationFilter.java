package com.manura.foodapp.security;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manura.foodapp.SpringApplicationContext;
import com.manura.foodapp.Service.impl.UserServiceImpl;
import com.manura.foodapp.UI.controller.Model.Req.UserLoginReq;
import com.manura.foodapp.shared.DTO.UserDto;
import com.manura.foodapp.shared.Utils.JWT.security.token.creator.TokenCreator;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final TokenCreator tokenCreator;


    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
 
        	UserLoginReq creds = new ObjectMapper()
                    .readValue(req.getInputStream(), UserLoginReq.class);
            
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>())
            );
            
        } catch (IOException e) {
            return null;
        }
    }
    
    @SneakyThrows
    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        
        String userName = ((UserPrincipal) auth.getPrincipal()).getUsername();  

        SignedJWT signedJWT = tokenCreator.createSignedJWT(auth);
        String encryptToken = tokenCreator.encryptToken(signedJWT);

        UserServiceImpl userService = (UserServiceImpl) SpringApplicationContext.getBean("userServiceImpl");
        UserDto userDto = userService.getUser(userName);
        
        res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + encryptToken);
        res.addHeader("UserID", userDto.getPublicId());

    }  

}
