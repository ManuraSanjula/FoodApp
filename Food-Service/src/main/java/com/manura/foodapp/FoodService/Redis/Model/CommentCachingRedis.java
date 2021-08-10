package com.manura.foodapp.FoodService.Redis.Model;

import java.io.Serializable;
import java.util.List;

import com.manura.foodapp.FoodService.entity.CommentsEntity;

import lombok.Data;

@Data
public class CommentCachingRedis implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = -9102845653603379031L;
/**
	 * 
	 */
	private String name;
	private List<CommentsEntity> comment;

}
