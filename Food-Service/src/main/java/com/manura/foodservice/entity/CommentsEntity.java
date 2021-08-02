package com.manura.foodservice.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class CommentsEntity implements Serializable {
    private static final long serialVersionUID = 1999887L;
    
    String description;
    String user;
    String userImage;
    Date createdAt;
}
