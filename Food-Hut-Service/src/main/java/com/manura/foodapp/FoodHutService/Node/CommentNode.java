package com.manura.foodapp.FoodHutService.Node;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Node("Comment")
@EqualsAndHashCode
public class CommentNode implements Serializable{

	private static final long serialVersionUID = 4834256680659096477L;
	@Id
	@GeneratedValue
	private Long id;
	private String publicId;
	private String comment;
	private String userImage;
	private Date createdAt;

	@Relationship(type = "CommentHasUser", direction = Direction.OUTGOING )
	private UserNode user;
	
	private Double rating;
}