package com.manura.foodapp.FoodService.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CommentsDto implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 420795218980620004L;
	String description;
    String userImage;
    Date createdAt;
    
    UserDto user;
    FoodDto food;
}
