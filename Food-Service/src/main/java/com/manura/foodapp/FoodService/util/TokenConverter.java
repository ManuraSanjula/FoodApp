package com.manura.foodapp.FoodService.util;

import java.text.ParseException;
import java.util.Date;

import com.manura.foodapp.FoodService.entity.UserEntity;
import com.manura.foodapp.FoodService.repo.UserRepo;
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
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenConverter {

    private final JwtConfiguration jwtConfiguration;
    private final UserRepo userRepo;
   

    public String decryptToken(String encryptedToken) throws ParseException, JOSEException {
        JWEObject jweObject = JWEObject.parse(encryptedToken);
        DirectDecrypter directDecrypter = new DirectDecrypter(jwtConfiguration.getPrivateKey().getBytes());
        jweObject.decrypt(directDecrypter);
        return jweObject.getPayload().toSignedJWT().serialize();
    }

   
    public Mono<UserEntity> validateTokenSignature(String token) {
    	UserEntity userEntity = new UserEntity();
    	userEntity.setPublicId("");
    	try {
    		String decryptUserToken = decryptToken(token);
            SignedJWT signedJWT = SignedJWT.parse(decryptUserToken);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            final Date expiration = claimsSet.getExpirationTime();
            Date todayDate = new Date();
            if (expiration.before(todayDate))
                return Mono.just(userEntity);
            RSAKey publicKey = RSAKey.parse(signedJWT.getHeader().getJWK().toJSONObject());
            if (!signedJWT.verify(new RSASSAVerifier(publicKey)))
                 return Mono.just(userEntity);;
        
            return Mono.just(userRepo.findByEmail(signedJWT.getPayload().toJSONObject().get("sub").toString()))
            		.switchIfEmpty(Mono.just(userEntity));
    	}catch (Exception e) {
    		  return Mono.just(userEntity);
		}
    }
}
