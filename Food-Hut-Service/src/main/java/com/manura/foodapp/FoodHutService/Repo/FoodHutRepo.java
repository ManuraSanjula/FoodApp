package com.manura.foodapp.FoodHutService.Repo;

import org.neo4j.springframework.data.repository.ReactiveNeo4jRepository;

import com.manura.foodapp.FoodHutService.Node.FoodHutNode;

import reactor.core.publisher.Mono;

public interface FoodHutRepo extends ReactiveNeo4jRepository<FoodHutNode, String>  {
  Mono<FoodHutNode> findByPublicId(String publicId);
}
