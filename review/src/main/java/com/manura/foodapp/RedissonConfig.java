/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.manura.foodapp;

/**
 *
 * @author Manura Sanjula
 */
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.Objects;
import javax.enterprise.inject.Produces;

public class RedissonConfig {

    private RedissonClient redissonClient;

    @Produces
    public RedissonClient getClient(){
        if(Objects.isNull(this.redissonClient)){
            Config config = new Config();
            config.useSingleServer()
                    .setAddress("redis://127.0.0.1:6379");
            redissonClient = Redisson.create(config);
        }
        return redissonClient;
    }

}
