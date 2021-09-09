package com.manura.foodapp.OrderService.Table.Support;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import lombok.Data;

@Data
@UserDefinedType("order_food_infromation") 
public class OrderFoodInfromation {
  private String foodId;
  private Long count;
  private Double price;
}
