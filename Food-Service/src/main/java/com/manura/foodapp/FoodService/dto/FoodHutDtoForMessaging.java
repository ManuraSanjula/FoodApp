package com.manura.foodapp.FoodService.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class FoodHutDtoForMessaging implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 5316571982360725610L;
	private String id;
	private String name;
    private String address;
    private Boolean open;
    private List<String> phoneNumbers;
    private String image;
    private Double latitude;
    private Double longitude;
}
