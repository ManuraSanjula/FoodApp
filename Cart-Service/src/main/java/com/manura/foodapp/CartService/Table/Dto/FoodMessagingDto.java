package com.manura.foodapp.CartService.Table.Dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class FoodMessagingDto implements Serializable, Comparable<FoodMessagingDto> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -112861927310419886L;
	private String id;
	private String name;
	private String publicId;
	private String description;
	private String type;
	private Integer unlikes;
	private Integer likes;
	private Double price;
	private Double rating;
	private Map<String, Integer> nutrition;
	private String coverImage;
	private List<String> images = new ArrayList<String>();
	private Boolean offered = true;

	@Override
	public int compareTo(FoodMessagingDto o) {
		// TODO Auto-generated method stub
		return this.id.equals(o.getId()) ? 1 : 0;
	}
}
