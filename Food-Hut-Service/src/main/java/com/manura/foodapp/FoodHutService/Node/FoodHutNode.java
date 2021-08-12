package com.manura.foodapp.FoodHutService.Node;

import java.util.List;

import org.neo4j.springframework.data.core.schema.Node;

import lombok.Data;

@Node("FoodHut")
@Data
public class FoodHutNode {
   private String name;
   private Integer groupSizePerTable;
   private Integer ratingsQuantity;
   private String summary;
   private String description;
   private String imageCover;
   private List<String> images;
   private String opentAt;
}
