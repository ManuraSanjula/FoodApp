package com.manura.foodapp.OrderService.dto;

import java.io.Serializable;

import lombok.Data;


@Data
public class FoodDto implements Serializable {
	private static final long serialVersionUID = 8753047797694557053L;
	private String name;
	private String description;
	private String type;
	private Integer unlikes;
	private Integer likes;
	private Double rating;
	private String coverImage;
	private FoodInfoDto foodInfo;
}
