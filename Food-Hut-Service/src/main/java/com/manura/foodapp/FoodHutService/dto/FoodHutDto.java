package com.manura.foodapp.FoodHutService.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
	private String address;
	private Boolean open;
	private String imageCover;
	private List<String> images;
	private List<String> phoneNumbers;
	private String opentAt;
	
	
	private Set<FoodHutHasCommentDto> comment = new HashSet<>();
	private Set<FoodHutHasFoodDto> foods = new HashSet<>();
}
