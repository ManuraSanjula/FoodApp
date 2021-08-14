package com.manura.foodapp.FoodHutService.Node;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import com.manura.foodapp.FoodHutService.FoodHutServiceApplication.UserNode;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Node("Comment")
@EqualsAndHashCode
public class CommentNode implements Serializable, Comparable<CommentNode> {

	private static final long serialVersionUID = 4834256680659096477L;
	@Id
	@GeneratedValue
	private Long id;
	private String publicId;
	private String comment;
	private String userImage;
	private Date createdAt;

	@Relationship(type = "CommentHasUser", direction = Relationship.Direction.INCOMING )
	private UserNode user;
	@Relationship(type = "CommentHasFoodHut", direction =   Relationship.Direction.INCOMING)
	private FoodHutNode foodHut;
	private Double rating;

	@Override
	public int compareTo(CommentNode o) {
		// TODO Auto-generated method stub
		return this.id.equals(o.getId()) ? 1 : 0;
	}
}