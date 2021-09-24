package com.manura.foodapp.FoodHutService;

import org.neo4j.driver.Driver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.core.DatabaseSelection;
import org.springframework.data.neo4j.core.ReactiveDatabaseSelectionProvider;
import org.springframework.data.neo4j.core.transaction.ReactiveNeo4jTransactionManager;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.config.EnableReactiveNeo4jRepositories;
import org.springframework.transaction.ReactiveTransactionManager;

import com.manura.foodapp.FoodHutService.Node.CommentNode;
import com.manura.foodapp.FoodHutService.Node.FoodHutNode;
import com.manura.foodapp.FoodHutService.Node.FoodNode;
import com.manura.foodapp.FoodHutService.Node.UserNode;
import reactor.core.publisher.Mono;

@SpringBootApplication(proxyBeanMethods = false)
@EnableReactiveNeo4jRepositories(considerNestedRepositories = true)
@EnableDiscoveryClient
public class FoodHutServiceApplication {

	public static final String KEY_DATABASE_NAME = "database";

	public static void main(String[] args) {
		SpringApplication.run(FoodHutServiceApplication.class, args);
	}

	public interface FoodRepo extends ReactiveNeo4jRepository<FoodNode, Long> {
		Mono<FoodNode> findByPublicId(String publicId);
	}

	public interface FoodHutRepo extends ReactiveNeo4jRepository<FoodHutNode, Long> {
		Mono<FoodHutNode> findByPublicId(String publicId);
	}

	public interface CommentRepo extends ReactiveNeo4jRepository<CommentNode, Long> {
		Mono<CommentNode> findByPublicId(String publicId);
		Mono<Void> deleteByPublicId(String id);
	}

	@Configuration(proxyBeanMethods = false)
	static class Neo4jConfig {

		@Bean
		public ReactiveDatabaseSelectionProvider reactiveDatabaseSelectionProvider(
				Neo4jDataProperties neo4jDataProperties) {

			return () -> Mono.deferContextual(
					ctx -> Mono.justOrEmpty(ctx.<String>getOrEmpty(KEY_DATABASE_NAME)).map(DatabaseSelection::byName)
							.switchIfEmpty(Mono.just(DatabaseSelection.byName(neo4jDataProperties.getDatabase()))));
		}

		@Bean
		public ReactiveTransactionManager reactiveTransactionManager(Driver driver,
				ReactiveDatabaseSelectionProvider databaseSelectionProvider) {
			return new ReactiveNeo4jTransactionManager(driver, databaseSelectionProvider);
		}
	}

	public interface UserRepo extends ReactiveNeo4jRepository<UserNode, Long> {
		Mono<UserNode> findByPublicId(String publicId);
		Mono<UserNode> findByEmail(String emial);
	}

}