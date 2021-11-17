package com.manura.foodapp.service;

import com.manura.foodapp.entity.UserEntity;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

@Stateless
public class ReviewService {
    
    @Inject
    RedissonClient client;
   
    public UserEntity saveUser(UserEntity dto) {
        RBucket<Object> bucket = client.getBucket(dto.getEmail());
        bucket.set(dto);
        return dto;
    }

}
