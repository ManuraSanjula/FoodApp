//package com.manura.foodapp.FoodService.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
//
//import com.mongodb.ConnectionString;
//import com.mongodb.MongoClientSettings;
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
//
//@Configuration
//@EnableReactiveMongoRepositories(basePackages = "com.manura.foodapp.FoodService.repo")
//public class SimpleMongoConfig {
//
//    @Bean
//    public MongoClient mongo() throws Exception {
//        final ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017/Food");
//        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder().applyConnectionString(connectionString).build();
//        return MongoClients.create(mongoClientSettings);
//    }
//
//    @Bean
//    public MongoTemplate mongoTemplate() throws Exception {
//        return new MongoTemplate(mongo(), "Food");
//    }
//
//}
