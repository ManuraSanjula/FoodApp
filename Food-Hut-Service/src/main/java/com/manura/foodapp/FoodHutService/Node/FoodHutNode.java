package com.manura.foodapp.FoodHutService.Node;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neo4j.springframework.data.core.schema.GeneratedValue;
import org.neo4j.springframework.data.core.schema.Id;
import org.neo4j.springframework.data.core.schema.Node;
import org.neo4j.springframework.data.core.schema.Relationship;
import org.springframework.data.geo.Point;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Node("FoodHut")
@Data
@EqualsAndHashCode
public class FoodHutNode implements Serializable, Comparable<FoodHutNode> {
	
	private static final long serialVersionUID = -1269695791365291936L;
	@Id
	@GeneratedValue
	private String id;
	String publicId;
	private String name;
	private Integer groupSizePerTable;
	private Integer ratingsQuantity;
	private String summary;
	private String description;
	private String imageCover;
	private List<String> images;
	private String opentAt;
	
	@Relationship(type = "COMMENT", direction = Relationship.Direction.INCOMING)
    private Set<CommentNode> comment = new HashSet<>();
	
	@Relationship(type = "FOOD", direction = Relationship.Direction.INCOMING)
    private Set<FoodNode> food = new HashSet<>();
    private Point location;
	
	@Override
	public int compareTo(FoodHutNode o) {
		// TODO Auto-generated method stub
		return this.id.equals(o.getId()) ? 1 :0;
	}
}
