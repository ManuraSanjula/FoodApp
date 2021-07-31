package com.manura.foodapp.security.filter;

import com.manura.foodapp.shared.Utils.JWT.security.property.JwtConfiguration;
import com.manura.foodapp.shared.Utils.JWT.security.token.converter.TokenConverter;
import com.manura.foodapp.shared.Utils.JWT.security.util.SecurityContextUtil;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

import static com.sun.xml.bind.v2.schemagen.Util.equalsIgnoreCase;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtTokenAuthorizationFilter extends OncePerRequestFilter {

    protected final JwtConfiguration jwtConfiguration;
    protected final TokenConverter tokenConverter;

    @Override

    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(jwtConfiguration.getHeader().getName());

        if (header == null || !header.startsWith(jwtConfiguration.getHeader().getPrefix())) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.replace(jwtConfiguration.getHeader().getPrefix(), "").trim();

        try{
            SecurityContextUtil.setSecurityContext(equalsIgnoreCase("signed", jwtConfiguration.getType()) ? validate(token,request) : decryptValidating(token,request));
        } catch (ParseException | JOSEException e) {
            return;
        }

        chain.doFilter(request, response);
    }


    private SignedJWT decryptValidating(String encryptedToken,HttpServletRequest request) throws ParseException, JOSEException {
        String signedToken = tokenConverter.decryptToken(encryptedToken);
        
        if(!tokenConverter.validateTokenSignature(signedToken,request))
            return null;
        return SignedJWT.parse(signedToken);
    }


    private SignedJWT validate(String signedToken,HttpServletRequest request) throws ParseException, JOSEException {
        if(!tokenConverter.validateTokenSignature(signedToken,request))
            return null;
        return SignedJWT.parse(signedToken);
    }
}
;