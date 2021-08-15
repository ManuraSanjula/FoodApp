package com.manura.foodapp.FoodHutService.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.geo.Point;

import com.manura.foodapp.FoodHutService.Node.Relationship.FoodHutHasComment;
import com.manura.foodapp.FoodHutService.Node.Relationship.FoodHutHasFood;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class FoodHutDto implements Serializable {
	private static final long serialVersionUID = 3900391630028803808L;
	private String id;
	private Integer zip;
	private String name;
	private Integer groupSizePerTable;
	private Integer ratingsQuantity;
	private String summary;
	private String description;
	private String imageCover;
	private List<String> images;
	private List<String> phoneNumbers;
	private String opentAt;
	private Set<FoodHutHasComment> comment = new HashSet<>();
	private Set<FoodHutHasFood> food = new HashSet<>();
	private Point location;
}
