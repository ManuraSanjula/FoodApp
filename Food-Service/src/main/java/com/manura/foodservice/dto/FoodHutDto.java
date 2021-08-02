package com.manura.foodservice.dto;

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
    List<String> foodIds;
    String image;
}
