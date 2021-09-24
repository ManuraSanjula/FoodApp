package com.manura.foodapp.FoodHutService.Controller.Req;

import java.io.Serializable;

import lombok.Data;

@Data
public class CommentReq implements Serializable{
	private static final long serialVersionUID = -4318721054209590026L;
	private String userId;
	private Double rating;
	private String comment;
}
