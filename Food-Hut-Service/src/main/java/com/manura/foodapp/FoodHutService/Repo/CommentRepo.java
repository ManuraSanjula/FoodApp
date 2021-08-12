package com.manura.foodapp.FoodHutService.Repo;

import org.neo4j.springframework.data.repository.ReactiveNeo4jRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.FoodHutService.Node.CommentNode;

import reactor.core.publisher.Mono;

@Repository
public interface CommentRepo extends ReactiveNeo4jRepository<CommentNode, String>  {
   Mono<CommentNode> findByPublicId(String publicId);
}
