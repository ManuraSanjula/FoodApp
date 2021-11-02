package com.manura.foodapp.UserService;

import com.manura.foodapp.UserService.security.AppProperties;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.ICredentialRepository;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
@Configuration
@EnableCaching
@EnableDiscoveryClient
public class UserMicroServiceApplication implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(UserMicroServiceApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean(name = "AppProperties")
	public AppProperties getAppProperties() {
		return new AppProperties();
	}

	@Bean
	public AuthenticationEventPublisher authenticationEventPublisher(ApplicationEventPublisher publisher) {
		return new DefaultAuthenticationEventPublisher(publisher);
	}

	@Bean
	public GoogleAuthenticator googleAuthenticator(ICredentialRepository credentialRepository) {
		GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder configBuilder = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder();
		configBuilder.setTimeStepSizeInMillis(TimeUnit.SECONDS.toMillis(60)).setWindowSize(10)
				.setNumberOfScratchCodes(0);
		GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator(configBuilder.build());
		googleAuthenticator.setCredentialRepository(credentialRepository);
		return googleAuthenticator;
	}

	@Override
	public void run(String... args) throws Exception {
	}
}
