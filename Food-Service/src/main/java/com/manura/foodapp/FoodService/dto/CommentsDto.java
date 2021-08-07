package com.manura.foodapp.FoodService.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class CommentsDto implements Serializable {
	private static final long serialVersionUID = 420795218980620004L;
    String id;
    String description;
    String userImage;
    Date createdAt;
    UserCommentDto user;
    FoodCommentDto food;
}
