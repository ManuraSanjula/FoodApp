package com.manura.foodservice.controller.Model.Res;

import lombok.Data;
import java.io.Serializable;
import java.util.Map;

@Data
public class HalfFoodRes implements Serializable {
    private static final long serialVersionUID = 6L;
    String id;
    String name;
    String description;
    String type;
    Map<String, Integer> nutrition;
    Boolean offered;
    String coverImage;
}
