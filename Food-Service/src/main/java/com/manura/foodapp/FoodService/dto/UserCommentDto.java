package com.manura.foodapp.FoodService.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserCommentDto implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6475102450278290353L;
	/**
	 * 
	 */
    private String firstName;
    private String lastName;
    private String email;
    private String pic;
}
