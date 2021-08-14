package com.manura.foodapp.FoodHutService.Node.Relationship;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;

import com.manura.foodapp.FoodHutService.Node.FoodNode;

import lombok.Data;

@Data
@RelationshipProperties
public class FoodHutHasFood {
	@Id
	@GeneratedValue
	private Long id;
	private FoodNode food;
}
