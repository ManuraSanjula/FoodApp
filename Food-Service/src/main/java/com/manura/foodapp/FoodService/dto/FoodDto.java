package com.manura.foodapp.FoodService.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class FoodDto implements Serializable,Comparable<FoodDto> {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1235802693254425596L;
	String id;
    String name;
    String description;
    String type;
    Integer unlikes;
    Integer likes;
    Double price;
    Double rating;
    Map<String, Integer> nutrition;
    String coverImage;
    List<String> images;
    Boolean offered;
    Set<FoodHutDto> foodHuts;
	@Override
	public int compareTo(FoodDto o) {
		// TODO Auto-generated method stub
		return this.id.equals(o.getId()) ? 1 :0;
	}
}
