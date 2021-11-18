package com.manura.foodapp;

import com.manura.foodapp.entity.UserEntity;
import com.manura.foodapp.service.ReviewService;
import java.text.ParseException;
import java.util.Date;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import javax.ejb.Stateless;
import javax.inject.Inject;



class JwtConfiguration {

    public int expiration = 3600;
    public int reseteExpiration = 600;
    public String privateKey = "qxBEEQv7E8aviX1KUcdOiF5ve5COUPAr";
    public String type = "encrypted";
}

@Stateless
public class TokenConverter {

    JwtConfiguration jwtConfiguration = new JwtConfiguration();
    
    @Inject
    ReviewService managementService;

    public String decryptToken(String encryptedToken) throws ParseException, JOSEException {
        JWEObject jweObject = JWEObject.parse(encryptedToken);
        DirectDecrypter directDecrypter = new DirectDecrypter(jwtConfiguration.privateKey.getBytes());
        jweObject.decrypt(directDecrypter);
        return jweObject.getPayload().toSignedJWT().serialize();
    }

    public UserEntity validateTokenSignature(String token) {

        try {
            String decryptUserToken = decryptToken(token);
            SignedJWT signedJWT = SignedJWT.parse(decryptUserToken);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            final Date expiration = claimsSet.getExpirationTime();
            Date todayDate = new Date();
            if (expiration.before(todayDate)) {
                return null;
            };
            RSAKey publicKey = RSAKey.parse(signedJWT.getHeader().getJWK().toJSONObject());
            if (!signedJWT.verify(new RSASSAVerifier(publicKey))) {
                return null;
            }
            String email = signedJWT.getPayload().toJSONObject().get("sub").toString();

            return managementService.getUser(email, token);
        } catch (Exception e) {
            return null;
        }
    }
}
