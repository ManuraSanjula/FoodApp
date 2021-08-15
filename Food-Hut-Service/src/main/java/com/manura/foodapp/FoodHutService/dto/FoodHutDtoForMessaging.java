package com.manura.foodapp.FoodHutService.dto;

import java.io.Serializable;
import java.util.List;


import lombok.Data;

@Data
public class FoodHutDtoForMessaging implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 531657193336725610L;
	private String id;
	private String name;
    private String address;
    private Boolean open;
    private List<String> phoneNumbers;
    private List<FoodDto> foods;
    private String image;
    private Double latitude;
    private Double longitude;
}
