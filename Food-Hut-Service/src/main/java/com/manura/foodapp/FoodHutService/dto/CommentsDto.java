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
	private static final long serialVersionUID = 7238769481312413923L;
	private String id;
	private String publicId;
	private String comment;
	private String userImage;
	private Date createdAt;
	private UserDto user;
	private FoodHutDto foodHut;
	private Double rating;
	
}
