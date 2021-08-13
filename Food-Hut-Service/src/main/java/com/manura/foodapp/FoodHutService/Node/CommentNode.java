package com.manura.foodapp.FoodHutService.Node;


import java.io.Serializable;
import java.util.Date;

import org.neo4j.springframework.data.core.schema.GeneratedValue;
import org.neo4j.springframework.data.core.schema.Id;
import org.neo4j.springframework.data.core.schema.Node;
import org.neo4j.springframework.data.core.schema.Relationship;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Node("Comment")
@EqualsAndHashCode
public class CommentNode implements Serializable, Comparable<CommentNode> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4834256680659096477L;
	@Id
	@GeneratedValue
	private String id;
	private String publicId;
	private String comment;
	private String userImage;
	private Date createdAt;

	@Relationship(type = "CommentHasUser", direction = Relationship.Direction.INCOMING )
	private UserNode user;
	@Relationship(type = "CommentHasFoodHut", direction =  Relationship.Direction.INCOMING)
	private FoodHutNode foodHut;
	private Double rating;

	@Override
	public int compareTo(CommentNode o) {
		// TODO Auto-generated method stub
		return this.id.equals(o.getId()) ? 1 : 0;
	}
}