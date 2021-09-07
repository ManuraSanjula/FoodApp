package com.manura.foodapp.UserService.Ui.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.manura.foodapp.UserService.Service.impl.UserServiceImpl;
import com.manura.foodapp.UserService.Ui.controller.Models.Request.PasswordResetModel;
import com.manura.foodapp.UserService.Ui.controller.Models.Request.PasswordResetRequestModel;
import com.manura.foodapp.UserService.Ui.controller.Models.Request.UserSignupReq;
import com.manura.foodapp.UserService.Ui.controller.Models.Request.UserUpdateReq;
import com.manura.foodapp.UserService.Ui.controller.Models.RequestOperationName;
import com.manura.foodapp.UserService.Ui.controller.Models.Response.OperationStatusModel;
import com.manura.foodapp.UserService.Ui.controller.Models.Response.RequestOperationStatus;
import com.manura.foodapp.UserService.Ui.controller.Models.Response.UserRes;
import com.manura.foodapp.UserService.UserServiceEvent.UserAccountSecurityOperationEvent;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.manura.foodapp.UserService.Ui.Errors.ErrorMessages;
import com.manura.foodapp.UserService.Ui.Errors.Exception.UserServiceException;
import com.manura.foodapp.UserService.UserServiceEvent.UserEvent;
import com.manura.foodapp.UserService.entity.UserEntity;
import com.manura.foodapp.UserService.repository.UserRepo;
import com.manura.foodapp.UserService.shared.AmazonSES;
import com.manura.foodapp.UserService.shared.DTO.UserDto;
import com.manura.foodapp.UserService.shared.Utils.Utils;

@Controller
class UserSecurityController{
	
	@RequestMapping("/users/emailVerify-WebPage")
	String emailVerifyWebPage(){
		return "EmailConfrim";
	}
}

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
    UserServiceImpl userService;

	@Autowired
	Utils util;

	@Autowired
	private AmazonSES amazonSES;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private UserRepo userRepo;

	@GetMapping(path = "/{email}")
	public UserRes getUser(@PathVariable String email, HttpServletResponse res, Authentication authentication) {
		UserEntity userDetails = (UserEntity) authentication.getPrincipal();
		ModelMapper modelMapper = new ModelMapper();
		if (userDetails.getFirstName().equals(email)) {
			UserDto userDto = userService.getUser(email);
			UserRes userRes = modelMapper.map(userDto, UserRes.class);
			return userRes;
		} else {
			throw new UserServiceException(ErrorMessages.AUTHENTICATION_FAILED.getErrorMessage());
		}

	}

	@PutMapping(path = "/{email}/profilePic")
	public UserRes uploadUserPic(@PathVariable String email, @RequestParam("pic") MultipartFile file,
			Authentication authentication) {
		UserEntity userDetails = (UserEntity) authentication.getPrincipal();
		if (userDetails.getFirstName().equals(email)) {
			return userService.uploadUserImage(email, file);
		} else {
			throw new UserServiceException(ErrorMessages.AUTHENTICATION_FAILED.getErrorMessage());

		}
	}

	@PostMapping("/signup")
	@ResponseStatus(HttpStatus.CREATED)
	public UserRes createUser(@RequestBody(required = false) UserSignupReq userSignupReq, HttpServletResponse res)
			throws IOException, InterruptedException {

		ModelMapper modelMapper = new ModelMapper();

		if (userSignupReq == null) {
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		}

		if (userSignupReq.getEmail() == null) {
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		}

		if (userSignupReq.getAddress() == null) {
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		}

		if (userSignupReq.getFirstName() == null) {
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		}

		if (userSignupReq.getLastName() == null) {
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		}

		if (!userSignupReq.getPassword().equals(userSignupReq.getConfirmPassword())) {
			throw new UserServiceException(ErrorMessages.AUTHENTICATION_FAILED.getErrorMessage());
		}

		UserDto userDto = modelMapper.map(userSignupReq, UserDto.class);

		UserDto createdUser = userService.createUser(userDto,"");
		
		UserRes userRes = modelMapper.map(createdUser, UserRes.class);

		UserEvent userEvent = new UserEvent(createdUser, rabbitTemplate, "userCreated");
		Thread thread = new Thread(userEvent);
		thread.start();

		return userRes;

	}
	
	@PostMapping("/signup/Admin")
	@ResponseStatus(HttpStatus.CREATED)
	public UserRes createSpecialUser(@RequestBody(required = false) UserSignupReq userSignupReq, HttpServletResponse res
			,@RequestParam(value = "role" , defaultValue = "") String role)
			throws IOException, InterruptedException {

		ModelMapper modelMapper = new ModelMapper();

		if (userSignupReq == null) {
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		}

		if (userSignupReq.getEmail() == null) {
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		}

		if (userSignupReq.getAddress() == null) {
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		}

		if (userSignupReq.getFirstName() == null) {
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		}

		if (userSignupReq.getLastName() == null) {
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		}

		if (!userSignupReq.getPassword().equals(userSignupReq.getConfirmPassword())) {
			throw new UserServiceException(ErrorMessages.AUTHENTICATION_FAILED.getErrorMessage());
		}

		UserDto userDto = modelMapper.map(userSignupReq, UserDto.class);

		UserDto createdUser = userService.createUser(userDto,role);
		
		UserRes userRes = modelMapper.map(createdUser, UserRes.class);

		UserEvent userEvent = new UserEvent(createdUser, rabbitTemplate, "userCreated");
		Thread thread = new Thread(userEvent);
		thread.start();

		return userRes;

	}


	@PutMapping(path = "/{email}")
	@Secured({ "ROLE_ADMIN", "ROLE_USER", "ROLE_CHEF" })
	public UserRes updateUser(@PathVariable String email, @RequestBody(required = false) UserUpdateReq userUpdateReq,
			Authentication authentication) {
		UserEntity userDetails = (UserEntity) authentication.getPrincipal();
		if (userDetails.getFirstName().equals(email)) {
			ModelMapper modelMapper = new ModelMapper();

			if (userUpdateReq == null) {
				throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
			}

			if (userUpdateReq.getEmail() != null) {
				throw new UserServiceException("Sorry Could not update Email !!");
			}

			UserDto userDto = modelMapper.map(userUpdateReq, UserDto.class);

			UserDto updateUser = userService.updateUser(email, userDto);
			UserRes returnValue = modelMapper.map(updateUser, UserRes.class);

			UserEvent userEvent = new UserEvent(updateUser, rabbitTemplate, "userUpdated");
			Thread thread = new Thread(userEvent);
			thread.start();

			return returnValue;
		} else {
			throw new UserServiceException(ErrorMessages.AUTHENTICATION_FAILED.getErrorMessage());
		}
	}

	@GetMapping(path = "/email-verification")
	public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {

		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

		try {
			boolean isVerified = userService.verifyEmailToken(token);

			if (isVerified) {
				returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
			} else {
				returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
			}

			return returnValue;
		} catch (Exception e) {
			returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
			return returnValue;
		}

	}

	@PostMapping(path = "/password-reset-request")
	public OperationStatusModel requestResetPassword(
			@RequestBody(required = false) PasswordResetRequestModel passwordResetRequestModel) {

		OperationStatusModel returnValue = new OperationStatusModel();

		boolean operationResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());

		returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
		returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

		if (operationResult) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}

		return returnValue;
	}

	@PostMapping(path = "/password-reset")
	public OperationStatusModel resetPassword(@RequestBody(required = false) PasswordResetModel passwordResetModel) {

		OperationStatusModel returnValue = new OperationStatusModel();

		boolean operationResult = userService.resetPassword(passwordResetModel.getToken(),
				passwordResetModel.getPassword());

		returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
		returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

		if (operationResult) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}
		return returnValue;
	}

	@SuppressWarnings("static-access")
	@GetMapping("/email-verification-request/{email}")
	public ResponseEntity<String> emailVerificationRequest(@PathVariable String email) {
		try {

			UserEntity userEntity = userRepo.findByEmail(email);
			if (userEntity == null)
				throw new UserServiceException("User Not Found");

			@SuppressWarnings("static-access")
            UserAccountSecurityOperationEvent emailVerification = new UserAccountSecurityOperationEvent(email,
					amazonSES);

			String verification = emailVerification.emailVerification(util.generateVerificationToken(email));
			userEntity.setEmailVerificationToken(verification);

			UserEntity resultEntity = userRepo.save(userEntity);
			if (resultEntity == null) {
				return new ResponseEntity<>("failure", HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			return new ResponseEntity<>("failure", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>("success", HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable String id) {
		userService.deleteUser(id);
		return new ResponseEntity<>("success", HttpStatus.OK);
	}

}
