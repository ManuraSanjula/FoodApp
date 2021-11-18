package com.manura.foodapp;

import com.manura.foodapp.entity.UserEntity;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;


@Provider
@Secure
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter {
    private static final String BEARER = "Bearer";
    @Inject
    TokenConverter converter;

    @Override
    public void filter(ContainerRequestContext reqCtx) throws IOException {
        String authHeader = reqCtx.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER)) {
            throw new NotAuthorizedException("No authorization header provided");
        }
        String token = authHeader.substring(BEARER.length()).trim();
        try {
            UserEntity user = converter.validateTokenSignature(token);
            if(user == null){
                 reqCtx.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            }else{
                 SecurityContext securityContext = reqCtx.getSecurityContext();
            reqCtx.setSecurityContext(new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return () -> user.getEmail();

                }

                @Override
                public boolean isUserInRole(String s) {
                    return securityContext.isUserInRole(s);
                }

                @Override
                public boolean isSecure() {
                    return securityContext.isSecure();
                }

                @Override
                public String getAuthenticationScheme() {
                    return securityContext.getAuthenticationScheme();
                }
            });
            

            //3. If parsing fails, yell.
            }
        } catch (Exception e) {
           
            //Another way to send exceptions to the programmatic
            reqCtx.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }

    }

}
