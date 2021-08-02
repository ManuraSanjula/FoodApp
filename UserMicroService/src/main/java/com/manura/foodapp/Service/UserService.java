package com.manura.foodapp.Service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import com.manura.foodapp.Ui.controller.Models.Response.UserRes;
import com.manura.foodapp.entity.UserEntity;
import com.manura.foodapp.shared.DTO.UserDto;

public interface UserService extends UserDetailsService {
	UserDto createUser(UserDto user);

	UserDto getUser(String email);
	
	UserRes uploadUserImage(String email,MultipartFile image);

	UserDto updateUser(String userId, UserDto user);

	UserDto deleteUser(String userId);

	boolean verifyEmailToken(String token);

	boolean requestPasswordReset(String email);

	boolean resetPassword(String token, String password);

	void saveUserIntoCache(UserEntity user);

	UserEntity getUserFromCache(String email);
}
