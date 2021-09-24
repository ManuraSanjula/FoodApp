package com.manura.foodapp.FoodService.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class FoodCommentDto implements Serializable, Comparable<FoodCommentDto> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7804908798377225432L;
	/**
	 * 
	 */
	String id;
	String name;
	String type;
	Integer unlikes;
	Integer likes;
	Double price;
	Double rating;
	String coverImage;
	List<String> images;

	@Override
	public int compareTo(FoodCommentDto o) {
		// TODO Auto-generated method stub
		return this.id.equals(o.getId()) ? 1 : 0;
	}
}
