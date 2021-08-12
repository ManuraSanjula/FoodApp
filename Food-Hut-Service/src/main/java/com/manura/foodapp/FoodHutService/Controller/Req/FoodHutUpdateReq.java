package com.manura.foodapp.FoodHutService.Controller.Req;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class FoodHutUpdateReq implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3318721054209590026L;
	private String name;
	private Integer groupSizePerTable;
	private Integer ratingsQuantity;
	private String summary;
	private String description;
	private String opentAt;
	private Double latitude; 
	private Double longitude;
	private  List<String> foodIds;
}
