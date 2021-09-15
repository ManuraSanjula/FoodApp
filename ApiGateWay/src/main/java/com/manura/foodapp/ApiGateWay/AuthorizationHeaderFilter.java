package com.manura.foodapp.ApiGateWay;

import java.util.Date;

import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.google.common.net.HttpHeaders;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import reactor.core.publisher.Mono;

@Configuration
class JwtConfiguration {
    public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public int getExpiration() {
		return expiration;
	}

	public void setExpiration(int expiration) {
		this.expiration = expiration;
	}

	public int getReseteExpiration() {
		return reseteExpiration;
	}

	public void setReseteExpiration(int reseteExpiration) {
		this.reseteExpiration = reseteExpiration;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@NestedConfigurationProperty
    private Header header = new Header();
    private int expiration = 3600;
    private int reseteExpiration = 600;
    private String privateKey = "qxBEEQv7E8aviX1KUcdOiF5ve5COUPAr";
    private String type = "encrypted";

    public static class Header {
        private String name = "Authorization";
        private String prefix = "Bearer ";
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getPrefix() {
			return prefix;
		}
		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}
        
    }
}

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
	
	private JwtConfiguration jwtConfiguration;
	
	public AuthorizationHeaderFilter() {
		super(Config.class);
	}
	
	public static class Config {
		
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {

			ServerHttpRequest request = exchange.getRequest();

			if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
				return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
			}

			String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
			String jwt = authorizationHeader.replace("Bearer", "");

			if (!isValidToken(jwt)) {
				return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
			}

			return chain.filter(exchange);
		};
	}
	
	private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(httpStatus);

		return response.setComplete();
	}
	
	private Boolean isValidToken(String token) {
		try {
			String decryptUserToken = decryptToken(token);
			SignedJWT signedJWT = SignedJWT.parse(decryptUserToken);
			JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
			final Date expiration = claimsSet.getExpirationTime();
			Date todayDate = new Date();
			if (expiration.before(todayDate))
				return false;
			return true;
		}catch (Exception e) {
			return true;
		}
	}
	
	private String decryptToken(String encryptedToken)  {
		try {
			JWEObject jweObject = JWEObject.parse(encryptedToken);
			DirectDecrypter directDecrypter = new DirectDecrypter(jwtConfiguration.getPrivateKey().getBytes());
			jweObject.decrypt(directDecrypter);
			return jweObject.getPayload().toSignedJWT().serialize();
		}catch (Exception e) {
			return null;
		}
	}
	
}
