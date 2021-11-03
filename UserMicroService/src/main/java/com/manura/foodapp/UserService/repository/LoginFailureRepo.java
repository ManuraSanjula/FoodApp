package com.manura.foodapp.UserService.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.UserService.entity.LoginFailure;
import com.manura.foodapp.UserService.entity.UserEntity;
@Repository
public interface LoginFailureRepo extends JpaRepository<LoginFailure, Integer> {
    List<LoginFailure> findAllByUserAndCreatedDateIsAfter(UserEntity user, Timestamp timestamp);
}
