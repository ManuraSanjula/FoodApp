package com.manura.foodapp.FoodHutService.Service;

import java.util.List;

import com.manura.foodapp.FoodHutService.Controller.Req.FoodHutUpdateReq;
import com.manura.foodapp.FoodHutService.Controller.Res.FoodHutHalfRes;
import com.manura.foodapp.FoodHutService.Node.FoodNode;
import com.manura.foodapp.FoodHutService.Node.UserNode;
import com.manura.foodapp.FoodHutService.dto.CommentsDto;
import com.manura.foodapp.FoodHutService.dto.FoodDto;
import com.manura.foodapp.FoodHutService.dto.FoodHutDto;
import com.manura.foodapp.FoodHutService.dto.UserDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FoodHutService {
  Mono<FoodHutDto> save(Mono<FoodHutDto> dto,List<String> foodIds,Double latitude, Double longitude);
  Mono<FoodHutDto> update(String id,Mono<FoodHutUpdateReq> dto);
  Flux<FoodHutHalfRes> getAll();
  Mono<FoodHutDto> getOne(String id);
  Mono<CommentsDto> addComment(Mono<CommentsDto> comment);
  Mono<UserDto> addUser(Mono<UserNode> user);
  Mono<UserDto> updateUser(String id,Mono<UserNode> user);
  Mono<FoodDto> addFood(Mono<FoodNode> food);
  Mono<FoodDto> updateFood(String id,Mono<FoodNode> food);
}
