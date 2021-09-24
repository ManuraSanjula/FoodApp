package com.manura.foodapp.FoodService.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class CommentsDto implements Serializable ,Comparable<CommentsDto>{
	private static final long serialVersionUID = 420795218980620004L;
    String id;
    String description;
    String userImage;
    Date createdAt;
    UserCommentDto user;
    FoodCommentDto food;
    Double rating;
	@Override
	public int compareTo(CommentsDto o) {
		// TODO Auto-generated method stub
		return this.id.equals(o.getId()) ? 1 :0;
	}
}
