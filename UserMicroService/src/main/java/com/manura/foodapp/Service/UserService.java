package com.manura.foodapp.Service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.manura.foodapp.entity.UserEntity;
import com.manura.foodapp.shared.DTO.UserDto;

public interface UserService extends UserDetailsService {
	UserDto createUser(UserDto user);

	UserDto getUser(String email);

	UserDto updateUser(String userId, UserDto user);

	UserDto deleteUser(String userId);

	boolean verifyEmailToken(String token);

	boolean requestPasswordReset(String email);

	boolean resetPassword(String token, String password);

	void saveUserIntoCache(UserEntity user);

	UserEntity getUserFromCache(String email);
}
