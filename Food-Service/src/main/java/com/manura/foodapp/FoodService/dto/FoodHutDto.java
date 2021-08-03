package com.manura.foodapp.FoodService.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class FoodHutDto implements Serializable {
    private static final long serialVersionUID = 9L;
    String id;
    String name;
    String address;
    Boolean open;
    List<String> phoneNumbers;
    List<FoodDto> foods;
    String image;
}
