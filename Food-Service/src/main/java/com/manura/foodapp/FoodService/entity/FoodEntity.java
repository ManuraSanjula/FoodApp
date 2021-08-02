package com.manura.foodapp.FoodService.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Document
@Data
@Builder
public class FoodEntity {
    @Id
    String id;
    String name;
    String publicId;
    String description;
    String type;
    @Builder.Default
    Integer unlikes = 0;
    @Builder.Default
    Integer likes = 0;
    @Builder.Default
    Integer price = 5;
    @Builder.Default
    Integer rating = 3;
    Map<String, Integer> nutrition;
    @Builder.Default
    String coverImage = "FoodCoverImage";
    @Builder.Default
    List<String> images = new ArrayList<String>();
    @Builder.Default
    Boolean offered = true;
    
    @Builder.Default
    List<CommentsEntity> comments = new ArrayList<CommentsEntity>();
    
    @Builder.Default
    List<FoodHutEntity> foodHuts =  new ArrayList<FoodHutEntity>();
}
