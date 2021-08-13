package com.manura.foodapp.FoodHutService.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.geo.Point;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class FoodHutDto implements Serializable, Comparable<FoodHutDto> {
	private static final long serialVersionUID = 3900391630028803808L;
	private String id;
	String publicId;
	private String name;
	private Integer groupSizePerTable;
	private Integer ratingsQuantity;
	private String summary;
	private String description;
	private String imageCover;
	private List<String> images;
	List<String> phoneNumbers;
	private String opentAt;
	private Set<CommentsDto> comment = new HashSet<>();
	private Set<FoodDto> food = new HashSet<>();
	private Point location;

	@Override
	public int compareTo(FoodHutDto o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
