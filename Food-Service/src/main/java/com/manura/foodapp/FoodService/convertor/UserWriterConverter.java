package com.manura.foodapp.FoodService.convertor;

import com.manura.foodapp.FoodService.entity.FoodEntity;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserWriterConverter implements Converter<FoodEntity, DBObject> {

    @Override
    public DBObject convert(final FoodEntity food) {
        final DBObject dbObject = new BasicDBObject();
        
        dbObject.put("id", food.getId());
        dbObject.put("name", food.getName());
        dbObject.put("publicId", food.getPublicId());
        dbObject.put("description", food.getDescription());
        dbObject.put("type", food.getType());
        dbObject.put("unlikes", food.getUnlikes());
        dbObject.put("likes", food.getLikes());
        dbObject.put("price", food.getPrice());
        dbObject.put("rating", food.getRating());
        dbObject.put("nutrition", food.getNutrition());
        dbObject.put("coverImage", food.getCoverImage());
        if (food.getImages() != null) {
            dbObject.put("images", food.getImages());
        }
        if (food.getComments() != null) {
            dbObject.put("comments", food.getComments());
        }
        if (food.getFoodHuts() != null) {
            dbObject.put("foodHuts", food.getFoodHuts());
        }
        dbObject.removeField("_class");
        return dbObject;
    }

}