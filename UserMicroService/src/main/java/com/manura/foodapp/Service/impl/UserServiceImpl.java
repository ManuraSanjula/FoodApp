package com.manura.foodapp.Service.impl;

import java.util.Arrays;
import java.util.Date;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.manura.foodapp.Service.UserService;
import com.manura.foodapp.Ui.Errors.ErrorMessages;
import com.manura.foodapp.Ui.Errors.Exception.UserServiceException;
import com.manura.foodapp.UserServiceEvent.UserEmailVerification;
import com.manura.foodapp.entity.AuthorityEntity;
import com.manura.foodapp.entity.RoleEntity;
import com.manura.foodapp.entity.UserEntity;
import com.manura.foodapp.repository.AuthorityRepo;
import com.manura.foodapp.repository.RoleRepo;
import com.manura.foodapp.repository.UserRepo;
import com.manura.foodapp.security.SecurityConstants;
import com.manura.foodapp.security.UserPrincipal;
import com.manura.foodapp.shared.AmazonSES;
import com.manura.foodapp.shared.DTO.UserDto;
import com.manura.foodapp.shared.Utils.Utils;
import com.manura.foodapp.shared.Utils.JWT.security.token.converter.TokenConverter;
import com.manura.foodapp.shared.Utils.JWT.security.token.creator.TokenCreator;
import com.nimbusds.jwt.SignedJWT;

import io.jsonwebtoken.Jwts;
import lombok.SneakyThrows;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private  UserRepo userRepo;
    
    private final String REDIS_HASH_KEY = "UserHash-UserService";
   
    @Autowired
    private  Utils util;

    @Autowired
    private TokenConverter tokenConverter;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private  RoleRepo roleRepo;

    @Autowired
    private AuthorityRepo authorityRepo;

    @Autowired
    private AmazonSES amazonSES;

    @Autowired
    private TokenCreator tokenCreator;
    
    @Autowired
    private	HashOperations<String, String, UserEntity> hashOps;
	

    @Override
    public UserDto createUser(UserDto user) {

        if (userRepo.findByEmail(user.getEmail()) != null) {
            throw new UserServiceException("User AlReady exits given Email");
        }

        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);

        AuthorityEntity readAuthority = authorityRepo.findByName("READ_AUTHORITY");
        AuthorityEntity writeAuthority = authorityRepo.findByName("WRITE_AUTHORITY");
        AuthorityEntity deleteAuthority = authorityRepo.findByName("DELETE_AUTHORITY");
        AuthorityEntity updateAuthority = authorityRepo.findByName("UPDATE_AUTHORITY");

        RoleEntity role = roleRepo.findByRole("ROLE_USER");
        role.setAuthorities(Arrays.asList(readAuthority, writeAuthority, updateAuthority, deleteAuthority));
        if (role != null) {
            userEntity.setRole(Arrays.asList(role));
        }

        userEntity.setPublicId(util.generateUserId(30));
        userEntity.setActive(true);
        userEntity.setEmailVerify(false);
        userEntity.setEmailVerify(false);
        userEntity.setPic("defaultPic");
        userEntity.setPasswordChangedAt(new Date());
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            UserEmailVerification emailVerification = new UserEmailVerification(user.getEmail(), amazonSES,
                    util.generatePasswordResetToken(user.getEmail()));

            userEntity.setEmailVerificationToken(emailVerification.getToken());
            emailVerification.run();
        } catch (Exception e) {

        }

        UserEntity createdUser = userRepo.save(userEntity);
        saveUserIntoCache(createdUser);
        UserDto result = modelMapper.map(createdUser, UserDto.class);

        result.setRoles(Arrays.asList("ROLE_USER"));
        result.setAuthorities(
                Arrays.asList("READ_AUTHORITY", "WRITE_AUTHORITY", "DELETE_AUTHORITY", "UPDATE_AUTHORITY"));

        return result;

    }

    @Override
    public UserDto getUser(String email) {
    	ModelMapper modelMapper = new ModelMapper();
    	UserEntity userFromCache =  getUserFromCache(email);
        if(userFromCache == null) {
        	UserEntity userEntity = userRepo.findByEmail(email);
            if (userEntity == null)
                throw new UsernameNotFoundException(email);
            saveUserIntoCache(userEntity);
            UserDto returnValue = modelMapper.map(userEntity, UserDto.class);
            return returnValue;
        }else {
        	 UserDto returnValue = modelMapper.map(userFromCache, UserDto.class);
        	 return returnValue;
        }
    }

    @Override
    public UserDto updateUser(String email, UserDto user) {
    	UserEntity userFromCache = getUserFromCache(email);
       if(userFromCache == null) {
    	   UserEntity userEntity = userRepo.findByPublicId(email);
    	   saveUserIntoCache(userEntity);
           if (userEntity == null)
               throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

           if (user.getFirstName() != null)
               userEntity.setFirstName(user.getFirstName());

           if (user.getLastName() != null)
               userEntity.setLastName(user.getLastName());

           if (user.getAddress() != null)
               userEntity.setAddress(user.getAddress());

           UserEntity updatedUserDetails = userRepo.save(userEntity);
           ModelMapper modelMapper = new ModelMapper();
           saveUserIntoCache(updatedUserDetails);
           UserDto returnValue = modelMapper.map(updatedUserDetails, UserDto.class);
           
           return returnValue;
       }else {
  
           if (user.getFirstName() != null)
        	   userFromCache.setFirstName(user.getFirstName());

           if (user.getLastName() != null)
        	   userFromCache.setLastName(user.getLastName());

           if (user.getAddress() != null)
        	   userFromCache.setAddress(user.getAddress());

           UserEntity updatedUserDetails = userRepo.save(userFromCache);
           ModelMapper modelMapper = new ModelMapper();
           saveUserIntoCache(updatedUserDetails);
           UserDto returnValue = modelMapper.map(updatedUserDetails, UserDto.class);

           return returnValue;
       }
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
    	UserEntity userFromCache =  getUserFromCache(email);
    	if(userFromCache == null) {
    		UserEntity userEntity = userRepo.findByEmail(email);
    		saveUserIntoCache(userEntity);
            if (userEntity == null)
                return null;
            return new UserPrincipal(userEntity);
    	}else {
    		return new UserPrincipal(userFromCache);
    	}
        
    }

    @Override
    public UserDto deleteUser(String email) {
        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = userRepo.findByEmail(email);
        if (userEntity == null)
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        userEntity.setActive(false);
        UserDto returnValue = modelMapper.map(userEntity, UserDto.class);
        return returnValue;
    }

    private String getUserFromToken(String token) {
        try {
            String user = Jwts.parser().setSigningKey(SecurityConstants.getTokenSecret()).parseClaimsJws(token)
                    .getBody().getSubject();
            return user;
        } catch (Exception e) {

            return null;
        }

    }

    @Override
    public boolean verifyEmailToken(String token) {
        boolean returnValue = false;
        String user = getUserFromToken(token);
        UserEntity userEntity = userRepo.findByEmailAndEmailVerificationToken(user, token);
        if (userEntity != null) {
            boolean hastokenExpired = Utils.hasTokenExpired(token);
            if (!hastokenExpired) {
                userEntity.setEmailVerify(Boolean.TRUE);
                userEntity.setEmailVerificationToken(null);
                userRepo.save(userEntity);
                returnValue = true;
            }
        }

        return returnValue;
    }

    @Override
    @SneakyThrows
    public boolean requestPasswordReset(String email) {

        UserEntity userEntity = userRepo.findByEmail(email);
        if (userEntity == null)
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        SignedJWT token = tokenCreator.createSignedJWT(email);

        if (token == null)
            return false;

        String encryptToken = tokenCreator.encryptToken(token);

        if (encryptToken == null)
            return false;

        userEntity.setPasswordResetToken(encryptToken);

        UserEntity updatedUserDetails = userRepo.save(userEntity);

        if (updatedUserDetails == null)
            return false;
        return true;
    }

    @Override
    public boolean resetPassword(String token, String password) {

        if (tokenConverter.passwordRestTokenValidating(token) == null)
            return false;

        UserEntity user = userRepo.findByEmail(tokenConverter.passwordRestTokenValidating(token));

        if (user == null) 
            return false;
        
        user.setPassword(passwordEncoder.encode(password));
        user.setPasswordResetToken(null);

        UserEntity updatedUserDetails = userRepo.save(user);

        if (updatedUserDetails == null)
            return false;

        return true;
    }

	@Override
	public void saveUserIntoCache(UserEntity user) {
		try {
			hashOps.put(REDIS_HASH_KEY, user.getEmail(), user);
		}catch (Exception e) {
			
		}
	}

	@Override
	public UserEntity getUserFromCache(String email) {
		try {
			return hashOps.get(REDIS_HASH_KEY, email);
		}catch (Exception e) {
			  return null;
		}
	}

}
