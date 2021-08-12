package com.manura.foodapp.FoodService.controller.Model.Res;

import lombok.Data;
import java.io.Serializable;
import java.util.Map;

@Data
public class HalfFoodRes implements Serializable, Comparable<HalfFoodRes> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 710691554780791810L;
	String id;
    String name;
    String description;
    String type;
    Map<String, Integer> nutrition;
    Boolean offered;
    String coverImage;
    Double rating;
	@Override
	public int compareTo(HalfFoodRes o) {
	
		return this.id.equals(o.getId()) ? 1 :0;
	}
}
