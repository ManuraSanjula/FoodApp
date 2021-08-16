package com.manura.foodapp.FoodHutService.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class CommentsDto implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1389117597069617528L;
	/**
	 * 
	 */
	private String id;
	private String comment;
	private String userImage;
	private Date createdAt;
	private UserDto user;
	private FoodHutDto foodHut;
	private Double rating;
	
}
