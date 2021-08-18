package com.manura.foodapp.FoodService.util;

import java.text.ParseException;
import java.util.Date;

import com.manura.foodapp.FoodService.entity.UserEntity;
import com.manura.foodapp.FoodService.repo.UserRepo;
import com.manura.foodapp.FoodService.service.impl.RedisServiceImpl;
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
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenConverter {

	private final JwtConfiguration jwtConfiguration;
	private final UserRepo userRepo;
	private final RedisServiceImpl redisServiceImpl;

	public String decryptToken(String encryptedToken) throws ParseException, JOSEException {
		JWEObject jweObject = JWEObject.parse(encryptedToken);
		DirectDecrypter directDecrypter = new DirectDecrypter(jwtConfiguration.getPrivateKey().getBytes());
		jweObject.decrypt(directDecrypter);
		return jweObject.getPayload().toSignedJWT().serialize();
	}

	private Mono<UserEntity> ifUserAbsentInCache(String email) {
		UserEntity userEntity = userRepo.findByEmail(email);
		if (userEntity != null) {
			return Mono.just(userEntity).publishOn(Schedulers.boundedElastic()).switchIfEmpty(Mono.empty())
					.subscribeOn(Schedulers.boundedElastic()).doOnNext(i -> {
						redisServiceImpl.addNewUser(i).subscribe();
					});
		} else {
			return Mono.empty();
		}
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
				return Mono.just(userEntity);

			String email = signedJWT.getPayload().toJSONObject().get("sub").toString();

			return redisServiceImpl.getUser(email).switchIfEmpty(ifUserAbsentInCache(email))
					.publishOn(Schedulers.boundedElastic()).switchIfEmpty(Mono.empty())
					.subscribeOn(Schedulers.boundedElastic());

		} catch (Exception e) {
			return Mono.just(userEntity);
		}
	}
}
