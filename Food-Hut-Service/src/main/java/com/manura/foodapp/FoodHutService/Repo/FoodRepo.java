package com.manura.foodapp.FoodHutService.Repo;

import org.neo4j.springframework.data.repository.ReactiveNeo4jRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.FoodHutService.Node.FoodNode;

import reactor.core.publisher.Mono;

@Repository
public interface FoodRepo extends ReactiveNeo4jRepository<FoodNode, String>  {
   Mono<FoodNode> findByPublicId(String publicId);
}
