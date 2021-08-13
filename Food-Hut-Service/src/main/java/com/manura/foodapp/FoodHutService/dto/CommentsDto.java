package com.manura.foodapp.FoodHutService.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class CommentsDto implements Serializable ,Comparable<CommentsDto>{
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
	@Override
	public int compareTo(CommentsDto o) {
		// TODO Auto-generated method stub
		return this.id.equals(o.getId()) ? 1 :0;
	}
}
