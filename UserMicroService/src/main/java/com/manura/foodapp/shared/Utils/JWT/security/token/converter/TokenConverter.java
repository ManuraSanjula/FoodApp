package com.manura.foodapp.shared.Utils.JWT.security.token.converter;

import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.manura.foodapp.Service.impl.UserServiceImpl;
import com.manura.foodapp.entity.UserEntity;
import com.manura.foodapp.repository.UserRepo;
import com.manura.foodapp.shared.Utils.JWT.security.property.JwtConfiguration;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenConverter {

    private final JwtConfiguration jwtConfiguration;
    private final UserRepo userRepo;
    private final UserServiceImpl userServiceImpl;

    public String decryptToken(String encryptedToken) throws ParseException, JOSEException {
        JWEObject jweObject = JWEObject.parse(encryptedToken);
        DirectDecrypter directDecrypter = new DirectDecrypter(jwtConfiguration.getPrivateKey().getBytes());
        jweObject.decrypt(directDecrypter);
        return jweObject.getPayload().toSignedJWT().serialize();
    }

    public String passwordRestTokenValidating(String token) {
        try {
            String decryptUserToken = decryptToken(token);
            return validateTokenSignature(decryptUserToken, token);
        } catch (ParseException e) {
            return null;
        } catch (JOSEException e) {
            return null;
        }
    }

    public String validateTokenSignature(String signedToken, String token) throws ParseException, JOSEException {

        SignedJWT signedJWT = SignedJWT.parse(signedToken);

        UserEntity user = userRepo.findByEmail(signedJWT.getPayload().toJSONObject().get("sub").toString());

        if (user == null)
            return null;

        if (user.getPasswordResetToken() == null)
            return null;

        if (!user.getPasswordResetToken().equals(token))
            return null;

        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
        final Date expiration = claimsSet.getExpirationTime();
        Date todayDate = new Date();

        if (expiration.before(todayDate))
            return null;

        RSAKey publicKey = RSAKey.parse(signedJWT.getHeader().getJWK().toJSONObject());
        if (!signedJWT.verify(new RSASSAVerifier(publicKey)))
            return null;

        String email = user.getEmail();
        if (email == null)
            return null;
        return email;
    }

    public boolean validateTokenSignature(String signedToken, HttpServletRequest request)
            throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(signedToken);
        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
        final Date expiration = claimsSet.getExpirationTime();
        Date todayDate = new Date();
        if (expiration.before(todayDate))
            return false;
        RSAKey publicKey = RSAKey.parse(signedJWT.getHeader().getJWK().toJSONObject());
        if (!signedJWT.verify(new RSASSAVerifier(publicKey)))
            return false;
        UserEntity userFromCache = userServiceImpl.getUserFromCache(signedJWT.getPayload().toJSONObject().get("sub").toString());
        if(userFromCache == null) {
        	UserEntity user = userRepo.findByEmail(signedJWT.getPayload().toJSONObject().get("sub").toString());
            if (user == null)
                return false;
            else
            	return true;
        }else {
        	return true;
        }
    }
}
