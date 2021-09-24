package com.manura.foodapp.FoodService.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class FoodHutDto implements Serializable,Comparable<FoodHutDto> {
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
	@Override
	public int compareTo(FoodHutDto o) {
		// TODO Auto-generated method stub
		return this.id.equals(o.getId()) ? 1 :0;
	}

}
