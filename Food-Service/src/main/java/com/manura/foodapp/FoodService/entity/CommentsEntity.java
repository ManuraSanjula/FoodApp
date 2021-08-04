package com.manura.foodapp.FoodService.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.manura.foodapp.FoodService.anotation.CascadeSave;

import lombok.Data;

@Data
@Document
public class CommentsEntity implements Serializable {
    private static final long serialVersionUID = 1999887L;
    
    @Id
    String id;
    
    String description;
    String userImage;
    Date createdAt;
    @DBRef(lazy = true)
    @CascadeSave
    UserEntity user;
    @DBRef(lazy = true)
    @CascadeSave
    FoodEntity food;
}
