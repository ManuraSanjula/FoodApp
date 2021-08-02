package com.manura.foodapp.FoodService.service;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import com.manura.foodapp.FoodService.dto.CommentsDto;
import com.manura.foodapp.FoodService.dto.FoodDto;
import com.manura.foodapp.FoodService.dto.FoodHutDto;

public interface FoodService {
    Flux<FoodDto> findAll();
    Flux<FoodDto> findByTypeAndName(String type, String name);
    Flux<FoodDto> findByName(String name);
    Flux<FoodDto> findByType(String type);
    Mono<FoodDto> findById(String id);
    Mono<FoodDto> save(Mono<FoodDto> foodDto, List<String> foodHutId);
    Mono<FoodDto> update(String id,Mono<FoodDto> foodDto,List<String> foodHutIds);
    Mono<CommentsDto> saveComment(String id,Mono<CommentsDto> comment);
    Mono<FoodHutDto> saveFoodHut( Mono<FoodHutDto> foodHut);
    Mono<FoodHutDto> updateFoodHut(Mono<FoodHutDto> foodHut,String id);
}
