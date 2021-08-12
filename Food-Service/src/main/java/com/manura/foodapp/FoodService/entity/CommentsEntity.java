package com.manura.foodapp.FoodService.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document
public class CommentsEntity implements Serializable,Comparable<CommentsEntity> {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5831567259692945543L;
	@Id
    String id;
    String description;
    String userImage;
    Date createdAt;
    @DBRef
    UserEntity user;
    @DBRef
    FoodEntity food;
    Double rating;
	@Override
	public int compareTo(CommentsEntity o) {
		// TODO Auto-generated method stub
		return this.id.equals(o.getId()) ? 1 :0;
		}
}