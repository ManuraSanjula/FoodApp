package com.manura.foodapp.FoodHutService.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class FoodDto implements Serializable{
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 8272833438538995040L;
	private String id;
	private String name;
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
