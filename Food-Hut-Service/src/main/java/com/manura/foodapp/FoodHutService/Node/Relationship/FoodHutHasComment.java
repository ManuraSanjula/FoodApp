package com.manura.foodapp.FoodHutService.Node.Relationship;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import com.manura.foodapp.FoodHutService.Node.CommentNode;

import lombok.Data;

@Data
@RelationshipProperties
public class FoodHutHasComment {
	@Id
	@GeneratedValue
	private Long id;
	@TargetNode
	private CommentNode comment;
}
