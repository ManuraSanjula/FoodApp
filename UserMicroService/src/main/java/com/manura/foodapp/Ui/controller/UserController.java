package com.manura.foodapp.UI.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import com.manura.foodapp.Event.UserCreateEvent;
import com.manura.foodapp.Event.UserEmailVerification;
import com.manura.foodapp.Event.UserUpdatedEvent;
import com.manura.foodapp.Service.impl.UserServiceImpl;
import com.manura.foodapp.UI.Error.ErrorMessages;
import com.manura.foodapp.UI.Error.Exception.UserServiceException;
import com.manura.foodapp.UI.Error.Exception.UserServiceNotFoundException;
import com.manura.foodapp.UI.controller.Model.RequestOperationName;
import com.manura.foodapp.UI.controller.Model.Req.PasswordResetModel;
import com.manura.foodapp.UI.controller.Model.Req.PasswordResetRequestModel;
import com.manura.foodapp.UI.controller.Model.Req.UserSignupReq;
import com.manura.foodapp.UI.controller.Model.Req.UserUpdateReq;
import com.manura.foodapp.UI.controller.Model.Res.OperationStatusModel;
import com.manura.foodapp.UI.controller.Model.Res.RequestOperationStatus;
import com.manura.foodapp.UI.controller.Model.Res.UserRes;
import com.manura.foodapp.entity.UserEntity;
import com.manura.foodapp.repository.UserRepo;
import com.manura.foodapp.shared.AmazonSES;
import com.manura.foodapp.shared.DTO.UserDto;
import com.manura.foodapp.shared.Utils.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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

    @GetMapping(path = "/u/{id}")
    public UserRes getUser(@PathVariable String id, HttpServletResponse res) {
        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = userService.getUserByUserId(id);
        res.addHeader("UserID", userDto.getPublicId());

        UserRes userRes = modelMapper.map(userDto, UserRes.class);
        return userRes;
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

        UserDto createdUser = userService.createUser(userDto);

        UserRes userRes = modelMapper.map(createdUser, UserRes.class);

        UserCreateEvent userCreateEvent = new UserCreateEvent(createdUser, rabbitTemplate);
        userCreateEvent.run();

        return userRes;

    }

    @PutMapping(path = "/{id}")
    @Secured({ "ROLE_ADMIN", "ROLE_USER", "ROLE_CHEF" })
    public UserRes updateUser(@PathVariable String id, @RequestBody(required = false) UserUpdateReq userUpdateReq) {

        ModelMapper modelMapper = new ModelMapper();

        if (userUpdateReq == null) {
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
        }

        if (userUpdateReq.getEmail() != null) {
            throw new UserServiceException("Sorry Could not update Email !!");
        }

        UserDto userDto = modelMapper.map(userUpdateReq, UserDto.class);

        UserDto updateUser = userService.updateUser(id, userDto);
        UserRes returnValue = modelMapper.map(updateUser, UserRes.class);

        UserUpdatedEvent userCreateEvent = new UserUpdatedEvent(updateUser, rabbitTemplate);
        userCreateEvent.run();

        return returnValue;
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
                throw new UserServiceNotFoundException("No User Found given token");
            }

            return returnValue;
        } catch (Exception e) {
            throw new UserServiceException("Invalid Token");
        }

    }

    @PostMapping(path = "/password-reset-request")
    public OperationStatusModel requestResetPassword(
            @RequestBody(required = false) PasswordResetRequestModel passwordResetRequestModel) {

        if (passwordResetRequestModel == null)
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());

        if (!operationResult)
            throw new UserServiceNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

        if (operationResult) {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }

        return returnValue;
    }

    @PostMapping(path = "/password-reset")
    public OperationStatusModel resetPassword(@RequestBody(required = false) PasswordResetModel passwordResetModel) {
        ;
        if (passwordResetModel == null)
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = userService.resetPassword(passwordResetModel.getToken(),
                passwordResetModel.getPassword());

        returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

        if (operationResult) {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        } else {
            throw new UserServiceException("Token Invalid");
        }

        return returnValue;
    }

    @GetMapping("/email-verification-request/{email}")
    public ResponseEntity<String> emailVerificationRequest(@PathVariable String email) {
        try {
            UserEmailVerification emailVerification = new UserEmailVerification(email, amazonSES,
                    util.generatePasswordResetToken(email));

            UserEntity userEntity = userRepo.findByEmail(email);
            if (userEntity == null)
                throw new UserServiceException("User Not Found");
                
            userEntity.setEmailVerificationToken(emailVerification.getToken());
            emailVerification.run();
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
