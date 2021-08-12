package com.manura.foodapp.FoodService.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserCommentDto implements Serializable,Comparable<UserCommentDto> {
   
	private static final long serialVersionUID = 6475102450278290353L;
	
    private String firstName;
    private String lastName;
    private String email;
	@Override
	public int compareTo(UserCommentDto o) {
		// TODO Auto-generated method stub
		return this.email.equals(o.getEmail()) ? 1 :0;
	}
}
