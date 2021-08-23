package com.manura.foodapp.FoodService.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

@Data
public class FoodHutDtoForSubSaving implements Serializable,Comparable<FoodHutDtoForSubSaving> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5316571982360725610L;
	String id;
    String name;
    String address;
    Boolean open;
    List<String> phoneNumbers;
    String image;
    private GeoJsonPoint location;

    @Override
	public int compareTo(FoodHutDtoForSubSaving o) {
		// TODO Auto-generated method stub
		return this.id.equals(o.getId()) ? 1 :0;
	}

}
