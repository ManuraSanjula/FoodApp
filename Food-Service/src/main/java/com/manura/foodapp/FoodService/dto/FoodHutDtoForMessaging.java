package com.manura.foodapp.FoodService.dto;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.geo.Point;

import lombok.Data;

@Data
public class FoodHutDtoForMessaging implements Serializable,Comparable<FoodHutDtoForMessaging> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5316571982360725610L;
	private String id;
	private String name;
    private String address;
    private Boolean open;
    private List<String> phoneNumbers;
    private List<FoodDto> foods;
    private String image;
    private Point location;
	@Override
	public int compareTo(FoodHutDtoForMessaging o) {
		// TODO Auto-generated method stub
		return this.id.equals(o.getId()) ? 1 :0;
	}

}
