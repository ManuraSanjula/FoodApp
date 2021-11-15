package com.manura.foodapp.service;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import org.modelmapper.ModelMapper;

import com.manura.foodapp.dto.UserDto;
import com.manura.foodapp.entity.UserEntity;

@Singleton
public class ReviewService {

    @Inject
    private EntityManager entityManager;

    private ModelMapper modelMapper = new ModelMapper();

    public UserEntity saveUser(UserDto dto) {
        UserEntity user = modelMapper.map(dto, UserEntity.class);
        entityManager.persist(user);
        return user;
    }

}
