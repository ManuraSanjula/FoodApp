package com.manura.foodapp.FoodHutService.Node;

import java.io.Serializable;
import java.util.Date;

import org.neo4j.springframework.data.core.schema.GeneratedValue;
import org.neo4j.springframework.data.core.schema.Node;
import org.neo4j.springframework.data.core.schema.Relationship;
import org.springframework.data.annotation.Id;
import lombok.Data;

@Data
@Node("Comment")
public class CommentNode implements Serializable, Comparable<CommentNode> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4834256680659096477L;
	@Id
	@GeneratedValue
	String id;
	String description;
	String userImage;
	Date createdAt;

	@Relationship(type = "CommentHasUser", direction = Relationship.Direction.OUTGOING)
	UserNode user;
	@Relationship(type = "CommentHasFoodHut", direction = Relationship.Direction.OUTGOING)
	FoodHutNode foodHut;
	Double rating;

	@Override
	public int compareTo(CommentNode o) {
		// TODO Auto-generated method stub
		return this.id.equals(o.getId()) ? 1 : 0;
	}
}