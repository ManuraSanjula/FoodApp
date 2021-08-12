package com.manura.foodapp.FoodHutService.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class CommentsDto implements Serializable ,Comparable<CommentsDto>{
    /**
	 * 
	 */
	private static final long serialVersionUID = 7238769481312413923L;
	String id;
    String description;
    String userImage;
    Date createdAt;
    UserDto user;
    FoodHutDto foodHut;
    Double rating;
	@Override
	public int compareTo(CommentsDto o) {
		// TODO Auto-generated method stub
		return this.id.equals(o.getId()) ? 1 :0;
	}
}
