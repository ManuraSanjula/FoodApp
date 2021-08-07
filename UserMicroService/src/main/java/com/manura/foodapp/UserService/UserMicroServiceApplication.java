package com.manura.foodapp.UserService;

import com.manura.foodapp.UserService.security.AppProperties;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@Configuration
public class UserMicroServiceApplication implements CommandLineRunner {

    @Autowired
    private  RabbitTemplate rabbitTemplate;
//
//    static String PLAIN_TEXT = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MkBnbWFpbC5jb20iLCJpYXQiOjE2MjIwODUyMTYsImV4cCI6MTYyMjEyMTIxNn0.d4sXt1LELA6l1pbJJmZ4KhMrpgHCXg5g8dTMQ-nL4lc";
//    static String ENCRYPTION_KEY = "mykey@91mykey@91";
//    static String INITIALIZATIO_VECTOR = "AODVNUASDNVVAOVF";

    public static void main(String[] args) {
//        try {
//
//            System.out.println("Plain text: " + PLAIN_TEXT);
//            byte[] encryptedMsg = Encryption.encrypt(PLAIN_TEXT, ENCRYPTION_KEY);
//            String base64Encrypted = Base64.getEncoder().encodeToString(encryptedMsg);
//            System.out.println("Encrypted: "+  base64Encrypted);
//
        //    byte[] base64Decrypted = Base64.getDecoder().decode(base64Encrypted);
        //    String decryptedMsg = Encryption.decrypt(base64Decrypted, ENCRYPTION_KEY);
        //    System.out.println("Decrypted: " + decryptedMsg);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        SpringApplication.run(UserMicroServiceApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = "AppProperties")
    public AppProperties getAppProperties() {
        return new AppProperties();
    }

    @Override
    public void run(String... args) throws Exception {
        rabbitTemplate.convertAndSend("UserCreated", "UserServiceStarted");
    }
}
