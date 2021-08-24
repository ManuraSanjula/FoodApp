package com.manura.foodapp.FoodHutService.Controller.Req;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class FoodHutReq implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3318000054209590026L;
	private Integer zip;
	private String publicId;
	private String name;
	private Integer groupSizePerTable;
	private Integer ratingsQuantity;
	private String summary;
	private String description;
	private String address;
	private Boolean open;
	private String openAt;
	private Double longitude;
	private Double latitude; 
	private  List<String> foodIds;
	List<String> phoneNumbers;
}
