package com.manura.foodservice.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.Map;
@Document
@Data
public class FoodEntity {
    @Id
    String id;
    String name;
    String publicId;
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
    List<CommentsEntity> comments;
    List<FoodHutEntity> foodHuts;
}
