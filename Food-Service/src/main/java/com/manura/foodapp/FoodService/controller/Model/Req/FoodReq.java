package com.manura.foodapp.FoodService.controller.Model.Req;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class FoodReq implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 769785207831035578L;
	String name;
    String description;
    String type;
    Double price;
    Map<String, Integer> nutrition;
    List<String> foodHutsIds;
}
