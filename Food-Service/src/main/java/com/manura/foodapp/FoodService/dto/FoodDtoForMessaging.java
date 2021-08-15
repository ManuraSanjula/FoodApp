package com.manura.foodapp.FoodService.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class FoodDtoForMessaging implements Serializable,Comparable<FoodDtoForMessaging> {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1235802693254425596L;
	private String publicId;
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
    List<FoodHutDto> foodHuts;
	@Override
	public int compareTo(FoodDtoForMessaging o) {
		// TODO Auto-generated method stub
		return this.publicId.equals(o.getPublicId()) ? 1 :0;
	}
}
