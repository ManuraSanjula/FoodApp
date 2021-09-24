package com.manura.foodapp.FoodService.service;


import java.util.List;

import org.springframework.data.geo.Distance;
import org.springframework.http.codec.multipart.FilePart;

import com.manura.foodapp.FoodService.controller.Model.Res.HalfFoodRes;
import com.manura.foodapp.FoodService.dto.CommentsDto;
import com.manura.foodapp.FoodService.dto.FoodDto;
import com.manura.foodapp.FoodService.dto.FoodHutDto;
import com.manura.foodapp.FoodService.dto.FoodHutDtoForSubSaving;
import com.manura.foodapp.FoodService.dto.UserDto;
import com.manura.foodapp.FoodService.entity.UserEntity;
import org.springframework.data.geo.Point;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FoodService {
	    Flux<HalfFoodRes> search(String regex);
	    Flux<HalfFoodRes> findAll(int page,int size);
	    Flux<CommentsDto> findAllComment(String foodId);
	    Flux<HalfFoodRes> findByTypeAndName(String type, String name,int page,int size);
	    Flux<HalfFoodRes> findByNames(String name,int page,int size);
	    Flux<HalfFoodRes> findByType(String type,int page,int size);
	    Mono<FoodDto> findById(String id);
	    Mono<FoodDto> save(Mono<FoodDto> foodDto, List<String> foodHutId);
	    Mono<FoodDto> update(String id,Mono<FoodDto> foodDto,List<String> foodHutIds);
	    Mono<CommentsDto> saveComment(String id,Mono<CommentsDto> comment,String user);
	    Mono<FoodHutDto> saveFoodHut( Mono<FoodHutDtoForSubSaving> foodHut);
	    Mono<FoodHutDto> updateFoodHut(Mono<FoodHutDtoForSubSaving> foodHut,String id);
	    Mono<UserEntity> getUser(String id);
	    Mono<UserEntity> saveUser(Mono<UserDto> user);
	    Mono<UserEntity> updateUser(String id,Mono<UserDto> user);
	    Mono<FoodDto> uploadCoverImage(String id,Mono<FilePart> filePartFlux);
	    Mono<FoodDto> uploadImages(String id,Flux<FilePart> filePartFlux);
	    Flux<HalfFoodRes> findByLocationNear(Point p, Distance d);
	    Mono<CommentsDto> updateComment(String id,String foodId,String desc);
	    Mono<Void> deleteComment(String foodId,String commentID);
	    Double avg();
}
