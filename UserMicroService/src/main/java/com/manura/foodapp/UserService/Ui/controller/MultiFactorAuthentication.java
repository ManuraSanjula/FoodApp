package com.manura.foodapp.UserService.Ui.controller;

import com.manura.foodapp.UserService.Ui.controller.Models.Response.MultiFactorAuthenticationRes;
import com.manura.foodapp.UserService.entity.UserEntity;
import com.manura.foodapp.UserService.repository.UserRepo;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/multiFactorAuthentication")
public class MultiFactorAuthentication {

	@Autowired
	private  GoogleAuthenticator googleAuthenticator;
	@Autowired
	private UserRepo userRepository;

	@GetMapping("/register2fa")
	public String register2fa(Model model) {
		UserEntity user = getUser();
		String url = GoogleAuthenticatorQRGenerator.getOtpAuthURL("SFG", user.getEmail(),
				googleAuthenticator.createCredentials(user.getEmail()));
		model.addAttribute("googleurl", url);
		return "user/register2fa";
	}

	@PostMapping("/register2fa")
	public MultiFactorAuthenticationRes confirm2Fa(@RequestParam Integer verifyCode) {
		UserEntity user = getUser();
		if (googleAuthenticator.authorizeUser(user.getEmail(), verifyCode)) {
			UserEntity savedUser = userRepository.findByEmail(user.getEmail());
			savedUser.setUseGoogle2f(true);
			userRepository.save(savedUser);
			return new MultiFactorAuthenticationRes(true);
		} else {
			return new MultiFactorAuthenticationRes(false);
		}
	}

	@GetMapping("/verify2fa")
	public String verify2fa() {
		return "user/verify2fa";
	}

	@PostMapping("/verify2fa")
	public MultiFactorAuthenticationRes verifyPostOf2Fa(@RequestParam Integer verifyCode) {
		UserEntity user = getUser();
		if (googleAuthenticator.authorizeUser(user.getEmail(), verifyCode)) {
			((UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
					.setGoogle2faRequired(false);
			return new MultiFactorAuthenticationRes(true);
		} else {
			return new MultiFactorAuthenticationRes(false);
		}
	}

	private UserEntity getUser() {
		return (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

}
