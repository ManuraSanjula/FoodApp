package com.manura.foodapp.CartService.Dto;

import java.io.Serializable;

import lombok.Data;


@Data
public class FoodDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8753047797694557053L;
	private String name;
	private String description;
	private String type;
	private Integer unlikes;
	private Integer likes;
	private Double price;
	private Double rating;
	private String coverImage;
}
