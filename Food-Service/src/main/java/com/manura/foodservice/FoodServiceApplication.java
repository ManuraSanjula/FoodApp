package com.manura.foodservice;

import com.manura.foodservice.dto.FoodHutDto;
import com.manura.foodservice.entity.UserEntity;
import com.manura.foodservice.repo.UserRepo;
import com.manura.foodservice.service.impl.FoodServiceImpl;
import com.manura.foodservice.util.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;

@SpringBootApplication
public class FoodServiceApplication implements CommandLineRunner {

    @Autowired
    FoodServiceImpl foodServiceImpl;

    @Autowired
    Utils utils;

    @Autowired
    UserRepo userRepo;

    public static void main(String[] args) {
        SpringApplication.run(FoodServiceApplication.class, args);
    }

    private void saveFoodHut() {
        FoodHutDto foodHutDto = new FoodHutDto();
        foodHutDto.setName("GalleFood Hut");
        foodHutDto.setId("1234567");
        foodHutDto.setAddress("Galle");
        foodHutDto.setImage("https://penarth.nub.news/uploads_news/kroxr_126/126_n_1_1475_1.jpg");
        foodHutDto.setPhoneNumbers(Arrays.asList("0382233803", "0718289525"));
        foodHutDto.setOpen(true);
        foodHutDto.setFoodIds(new ArrayList<String>());
        // Mono<FoodHutDto> h =
        // foodServiceImpl.updateFoodHut(Mono.just(foodHutDto),"1234567");
        Mono<FoodHutDto> h = foodServiceImpl.saveFoodHut(Mono.just(foodHutDto));
        h.subscribe(i -> {

        });
    }

    private void updateFoodHut() {
        FoodHutDto foodHutDto = new FoodHutDto();
        foodHutDto.setName("Panadura Food Hut !!!");
        foodHutDto.setId("1234567");
        foodHutDto.setAddress("Panadura Town");
        foodHutDto.setImage("https://penarth.nub.news/uploads_news/kroxr_126/126_n_1_1475_1.jpg");
        foodHutDto.setPhoneNumbers(Arrays.asList("0382233803", "0718289525"));
        foodHutDto.setOpen(true);
        foodHutDto.setFoodIds(Arrays.asList("60cb70c4e6304327696c1d94"));
        Mono<FoodHutDto> h = foodServiceImpl.updateFoodHut(Mono.just(foodHutDto), "1234567");
        h.subscribe(i -> {

        });
    }

    private void saveUser() {
        UserEntity user = new UserEntity();
        user.setAddress("Panadura");
        user.setId(11L);
        user.setFirstName("Manura");
        user.setLastName("Sanjula");
        user.setEmail("w.m.manurasanjula12345@gmail.com");
        user.setEmailVerify(true);
        user.setPassword("123456789");
        user.setRoles(Arrays.asList("ROLE_ADMIN"));
        user.setPublicId(utils.generateId(30));
        userRepo.save(user).subscribe(i->{});
    }

    @Override
    public void run(String... args) throws Exception {
        //saveUser();
        // saveFoodHut();
    }
}
