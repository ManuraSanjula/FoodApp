package com.manura.foodapp.FoodHutService.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode
public class FoodDto implements Serializable,Comparable<FoodDto> {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 8272833438538995040L;
	private String id;
	private String publicId;
	private String name;
	private String description;
	private String type;
	private Integer unlikes;
	private Integer likes;
	private Double price;
	private Double rating;
	private Map<String, Integer> nutrition;
	private String coverImage;
	private List<String> images;
	private Boolean offered;
	private List<FoodHutDto> foodHuts;
	@Override
	public int compareTo(FoodDto o) {
		// TODO Auto-generated method stub
		return this.id.equals(o.getId()) ? 1 :0;
	}
}
