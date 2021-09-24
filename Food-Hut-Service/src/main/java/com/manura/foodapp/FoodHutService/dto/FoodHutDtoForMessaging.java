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
	private Integer zip;
	private String name;
	private Integer groupSizePerTable;
	private Integer ratingsQuantity;
	private String summary;
	private String description;
	private String address;
	private Boolean open;
	private String imageCover;
	private List<String> images;
	private List<String> phoneNumbers;
	private String opentAt;
    private Double latitude;
    private Double longitude;
    
    
}
