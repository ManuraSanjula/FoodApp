package com.manura.foodapp.UserService.shared.Utils.JWT.security.token.creator;

import com.manura.foodapp.UserService.shared.Utils.JWT.security.property.JwtConfiguration;
import com.manura.foodapp.UserService.security.SecurityConstants;
import com.manura.foodapp.UserService.security.UserPrincipal;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenCreator {
    private final JwtConfiguration jwtConfiguration;

    public SignedJWT createSignedJWT(Authentication auth) throws NoSuchAlgorithmException, JOSEException {

        UserPrincipal applicationUser = (UserPrincipal) auth.getPrincipal();
        JWTClaimsSet jwtClaimSet = createJWTClaimSet(auth, applicationUser);
        KeyPair rsaKeys = generateKeyPair();
        JWK jwk = new RSAKey.Builder((RSAPublicKey) rsaKeys.getPublic()).keyID(UUID.randomUUID().toString()).build();
        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).jwk(jwk).type(JOSEObjectType.JWT).build(), jwtClaimSet);
        RSASSASigner signer = new RSASSASigner(rsaKeys.getPrivate());
        signedJWT.sign(signer);
        return signedJWT;

    }

    public SignedJWT createSignedJWT(String email) throws NoSuchAlgorithmException, JOSEException {
        JWTClaimsSet jwtClaimSet = createJWTClaimSet(email);
        KeyPair rsaKeys = generateKeyPair();
        JWK jwk = new RSAKey.Builder((RSAPublicKey) rsaKeys.getPublic()).keyID(UUID.randomUUID().toString()).build();
        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).jwk(jwk).type(JOSEObjectType.JWT).build(), jwtClaimSet);
        RSASSASigner signer = new RSASSASigner(rsaKeys.getPrivate());
        signedJWT.sign(signer);
        return signedJWT;

    }

    private JWTClaimsSet createJWTClaimSet(String email) {
        return new JWTClaimsSet.Builder().subject(email)
                .issuer("Manura Sanjula").issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + SecurityConstants.PASSWORD_RESET_EXPIRATION_TIME)).build();
    }

    private JWTClaimsSet createJWTClaimSet(Authentication auth, UserPrincipal applicationUser) {

        return new JWTClaimsSet.Builder().subject(applicationUser.getUsername())
                .claim("authorities",
                        auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toList()))
                .issuer("Manura Sanjula").issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME)).build();
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");

        generator.initialize(2048);
        return generator.genKeyPair();
    }

    public String encryptToken(SignedJWT signedJWT) throws JOSEException {
        DirectEncrypter directEncrypter = new DirectEncrypter(jwtConfiguration.getPrivateKey().getBytes());

        JWEObject jweObject = new JWEObject(
                new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256).contentType("JWT").build(),
                new Payload(signedJWT));
        jweObject.encrypt(directEncrypter);

        return jweObject.serialize();
    }
}
