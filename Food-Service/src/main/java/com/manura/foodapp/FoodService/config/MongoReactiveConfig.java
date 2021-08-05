package com.manura.foodapp.FoodService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import com.manura.foodapp.FoodService.event.CascadeSaveMongoEventListener;
import com.manura.foodapp.FoodService.event.FoodCascadeSaveMongoEventListener;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.manura.foodapp.FoodService.repo")
public class MongoReactiveConfig extends AbstractReactiveMongoConfiguration {

    @Override
    public MongoClient reactiveMongoClient() {
        return MongoClients.create();
    }
    
    @Bean
    public FoodCascadeSaveMongoEventListener userCascadingMongoEventListener() {
        return new FoodCascadeSaveMongoEventListener();
    }

    @Bean
    public CascadeSaveMongoEventListener cascadingMongoEventListener() {
        return new CascadeSaveMongoEventListener();
    }


    @Override
    protected String getDatabaseName() {
        return "Food";
    }
}
