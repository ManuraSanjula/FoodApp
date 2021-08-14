package com.manura.foodapp.FoodService.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document
public class CommentsEntity implements Serializable, Comparable<CommentsEntity> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5831567259692945543L;
	@Id
	private String id;
	private String description;
	private String userImage;
	private Date createdAt;
	@DBRef
	private UserEntity user;
	@DBRef
	private FoodEntity food;
	private Double rating;

	@Override
	public int compareTo(CommentsEntity o) {
		// TODO Auto-generated method stub
		return this.id.equals(o.getId()) ? 1 : 0;
	}
}