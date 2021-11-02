package com.manura.foodapp.UserService.repository;

import java.sql.Timestamp;
import java.util.List;

import com.manura.foodapp.UserService.entity.UserEntity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends CrudRepository<UserEntity, Long> {
   UserEntity findByEmail(String email);

   UserEntity findByPublicId(String userId);

   UserEntity findUserByEmailVerificationToken(String token);

   UserEntity findByEmailAndEmailVerificationToken(String email, String token);

   UserEntity findByEmailAndPasswordResetToken(String email, String token);

   @Query(value = "select * from User.users where User.users.password_reset_token=:passwordresettoken", nativeQuery = true)
   List<UserEntity> findByPasswordResetToken(@Param("passwordresettoken") String passwordresettoken);
   
   List<UserEntity> findAllByAccountNonLockedAndLastModifiedDateIsBefore(Boolean locked, Timestamp timestamp);
}
