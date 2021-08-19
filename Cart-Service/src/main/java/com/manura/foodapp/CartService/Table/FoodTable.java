package com.manura.foodapp.CartService.Table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Table("Food")
@Data
public class FoodTable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5270310274724521373L;
	
	@Id
	private Long id;
	private String name;
	private String publicId;
	private String description;
	private String type;
	private Integer unlikes;
	private Integer likes;
	private Double price;
	private Double rating;
	private String coverImage;

	private List<String> images = new ArrayList<String>();
	private Boolean offered = true;
}
