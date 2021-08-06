package com.manura.foodapp.FoodService.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class FoodHutDto implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5316571982360725610L;
	String id;
    String name;
    String address;
    Boolean open;
    List<String> phoneNumbers;
    List<FoodDto> foods;
    String image;
}
