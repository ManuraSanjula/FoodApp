package com.manura.foodapp.FoodHutService.Repo;


import org.neo4j.springframework.data.repository.ReactiveNeo4jRepository;

import com.manura.foodapp.FoodHutService.Node.UserNode;

import reactor.core.publisher.Mono;

public interface UserRepo extends ReactiveNeo4jRepository<UserNode, String>  {
  Mono<UserNode> findByPublicId(String publicId);
}
