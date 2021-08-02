package com.manura.foodservice.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class FoodDto implements Serializable {
    private static final long serialVersionUID = 8L;
    String id;
    String name;
    String description;
    String type;
    Integer unlikes;
    Integer likes;
    Integer price;
    Integer rating;
    Map<String, Integer> nutrition;
    String coverImage;
    List<String> images;
    Boolean offered;
    List<CommentsDto> comments;
    List<FoodHutDto> foodHuts;
}
