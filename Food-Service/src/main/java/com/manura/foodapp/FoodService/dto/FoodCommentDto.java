package com.manura.foodapp.FoodService.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class FoodCommentDto implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7804908798377225432L;
	/**
	 * 
	 */
	String id;
    String name;
    String type;
    Integer unlikes;
    Integer likes;
    Integer price;
    Integer rating;
    String coverImage;
    List<String> images;
}
