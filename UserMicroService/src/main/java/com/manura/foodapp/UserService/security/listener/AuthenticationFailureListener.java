package com.manura.foodapp.UserService.security.listener;

import lombok.RequiredArgsConstructor;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import com.manura.foodapp.UserService.entity.LoginFailure;
import com.manura.foodapp.UserService.entity.UserEntity;
import com.manura.foodapp.UserService.repository.LoginFailureRepository;
import com.manura.foodapp.UserService.repository.UserRepo;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;


@Component
@RequiredArgsConstructor
public class AuthenticationFailureListener {

    private final LoginFailureRepository loginFailureRepository;
    private final UserRepo userRepository;

    @EventListener
    public void listen(AuthenticationFailureBadCredentialsEvent event) {
        if (event.getSource() instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();
            LoginFailure.LoginFailureBuilder builder = LoginFailure.builder();

            if (token.getPrincipal() instanceof String) {
                builder.username((String) token.getPrincipal());
                UserEntity findByEmail = userRepository.findByEmail((String) token.getPrincipal());
                builder.user(findByEmail);
            }

            if (token.getDetails() instanceof WebAuthenticationDetails) {
                WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();                
                builder.sourceIp(details.getRemoteAddress());
            }
            LoginFailure failure = loginFailureRepository.save(builder.build());
            if (failure.getUser() != null) {
                lockUserAccount(failure.getUser());
            }
        }


    }

    private void lockUserAccount(UserEntity user) {
        List<LoginFailure> failures = loginFailureRepository.findAllByUserAndCreatedDateIsAfter(user,
                Timestamp.valueOf(LocalDateTime.now().minusDays(1)));

        if(failures.size() > 3){
            user.setAccountNonLocked(false);
            userRepository.save(user);
        }
    }







}