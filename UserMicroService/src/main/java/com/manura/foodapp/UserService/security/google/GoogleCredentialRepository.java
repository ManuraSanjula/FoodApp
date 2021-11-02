package com.manura.foodapp.UserService.security.google;

import com.manura.foodapp.UserService.entity.UserEntity;
import com.manura.foodapp.UserService.repository.UserRepo;
import com.warrenstrange.googleauth.ICredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@RequiredArgsConstructor
@Component
public class GoogleCredentialRepository implements ICredentialRepository {

    private final UserRepo userRepository;

    @Override
    public String getSecretKey(String userName) {
        UserEntity user = userRepository.findByEmail(userName);

        return user.getGoogle2FaSecret();
    }

    @Override
    public void saveUserCredentials(String userName, String secretKey, int validationCode, List<Integer> scratchCodes) {
    	UserEntity user = userRepository.findByEmail(userName);
        user.setGoogle2FaSecret(secretKey);
        user.setUseGoogle2f(true);
        userRepository.save(user);
    }
}
