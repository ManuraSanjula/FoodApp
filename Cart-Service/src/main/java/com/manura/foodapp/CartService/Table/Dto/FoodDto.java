package com.manura.foodapp.CartService.Table.Dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;


@Data
public class FoodDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8753047797694557053L;
	private Long id;
	private String name;
	private String publicId;
	private String description;
	private String type;
	private Integer unlikes;
	private Integer likes;
	private Double price;
	private Double rating;
	private String coverImage;

	private List<String> images = new ArrayList<String>();
	private Boolean offered = true;
}
