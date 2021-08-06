package com.manura.foodapp.FoodService.controller.Model.Req;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommentReq implements Serializable {
    private static final long serialVersionUID = 1L;
    String description;
}
