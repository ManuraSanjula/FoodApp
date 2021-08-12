package com.manura.foodapp.FoodHutService.Controller.Res;

import java.io.Serializable;

import org.springframework.data.geo.Point;

import lombok.Data;

@Data
public class FoodHutHalfRes implements Serializable, Comparable<FoodHutHalfRes> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2566575723145687983L;
	String publicId;
	private String name;
	private Integer groupSizePerTable;
	private Integer ratingsQuantity;
	private String imageCover;
	private String opentAt;
	private Point location;

	@Override
	public int compareTo(FoodHutHalfRes o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
