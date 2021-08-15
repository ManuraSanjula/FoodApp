package com.manura.foodapp.FoodHutService.Node;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.geo.Point;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import com.manura.foodapp.FoodHutService.Node.Relationship.FoodHutHasComment;
import com.manura.foodapp.FoodHutService.Node.Relationship.FoodHutHasFood;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Node("FoodHut")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FoodHutNode implements Serializable{
	private static final long serialVersionUID = -1269695791365291936L;
	@Id
	@GeneratedValue
	private Long id;
	private Integer zip;
	private String publicId;
	private String name;
	private Integer groupSizePerTable;
	private Integer ratingsQuantity;
	private String summary;
	private String description;
	private String imageCover;
	private List<String> images;
	private List<String> phoneNumbers;
	private String opentAt;

	@Relationship(type = "FOODHUT_HAS_COMMENTS", direction = Relationship.Direction.OUTGOING)
	private Set<FoodHutHasComment> comment = new HashSet<>();

	@Relationship(type = "FOODHUT_HAS_FOODS", direction = Relationship.Direction.OUTGOING)
	private Set<FoodHutHasFood> food = new HashSet<>();
	private Point location;
}