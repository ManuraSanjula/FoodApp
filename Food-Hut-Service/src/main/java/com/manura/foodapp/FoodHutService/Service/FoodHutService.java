package com.manura.foodapp.FoodHutService.Service;

import java.util.List;

import org.springframework.http.codec.multipart.FilePart;

import com.manura.foodapp.FoodHutService.Node.FoodNode;
import com.manura.foodapp.FoodHutService.Node.UserNode;
import com.manura.foodapp.FoodHutService.Controller.Req.CommentReq;
import com.manura.foodapp.FoodHutService.Controller.Req.FoodHutUpdateReq;
import com.manura.foodapp.FoodHutService.Controller.Res.FoodHutHalfRes;

import com.manura.foodapp.FoodHutService.dto.CommentsDto;
import com.manura.foodapp.FoodHutService.dto.FoodHutDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FoodHutService {
  Mono<FoodHutDto> save(Mono<FoodHutDto> dto,List<String> foodIds,Double latitude, Double longitude);
  Mono<FoodHutDto> update(String id,Mono<FoodHutUpdateReq> dto);
  Flux<FoodHutHalfRes> getAll();
  Mono<FoodHutDto> getOne(String id);
  Mono<UserNode> getUser(String user);
  Mono<CommentsDto> addComment(String id,Mono<CommentReq> comment);
  Mono<UserNode> addUser(Mono<UserNode> user);
  Mono<UserNode> updateUser(String id,Mono<UserNode> user);
  Mono<FoodNode> addFood(Mono<FoodNode> food);
  Mono<FoodNode> updateFood(String id,Mono<FoodNode> food);
  Mono<CommentsDto> updateComment(String foodHutId,String commentId,String comment);
  Flux<CommentsDto> getAllComments(String id);
  Mono<Void> deleteComment(String foodHutId,String commentId);
  Mono<FoodHutDto> uploadCoverImage(String id,Mono<FilePart> filePartFlux);
  Mono<FoodHutDto> uploadImages(String id,Flux<FilePart> filePartFlux);
}
