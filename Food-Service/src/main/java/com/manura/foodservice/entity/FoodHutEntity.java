package com.manura.foodservice.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;

@Data
@Document
public class FoodHutEntity {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodHutEntity that = (FoodHutEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(address, that.address) && Objects.equals(open, that.open) && Objects.equals(phoneNumbers, that.phoneNumbers) && Objects.equals(foodIds, that.foodIds) && Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address, open, phoneNumbers, foodIds, image);
    }

    @Id
    String id;
    String name;
    String address;
    Boolean open;
    List<String> phoneNumbers;
    List<String> foodIds;
    String image;
}
