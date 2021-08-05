//package com.manura.foodapp.FoodService.converter;
//
//import com.mongodb.BasicDBObject;
//import com.mongodb.DBObject;
//import com.manura.foodapp.FoodService.entity.FoodEntity;
//
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.stereotype.Component;
//
//@Component
//public class FoodWriterConverter implements Converter<FoodEntity, DBObject> {
//
//    @Override
//    public DBObject convert(final FoodEntity food) {
//        final DBObject dbObject = new BasicDBObject();
//        dbObject.put("id", food.getId());
//        dbObject.put("name", food.getName());
//        dbObject.put("publicId", food.getPublicId());
//        dbObject.put("description", food.getDescription());
//        dbObject.put("type", food.getType());
//        dbObject.put("unlikes", food.getUnlikes());
//        dbObject.put("likes", food.getLikes());
//        dbObject.put("price", food.getPrice());
//        dbObject.put("rating", food.getRating());
//        dbObject.put("nutrition", food.getNutrition());
//        dbObject.put("coverImage", food.getCoverImage());
//        dbObject.put("images", food.getImages());
//        dbObject.put("offered", food.getOffered());
//        if(food.getComments() != null) {
//        	final DBObject comentDbObject = new BasicDBObject();
//        	comentDbObject
//        }
////        if (user.getEmailAddress() != null) {
////            final DBObject emailDbObject = new BasicDBObject();
////            emailDbObject.put("value", user.getEmailAddress().getValue());
////            dbObject.put("email", emailDbObject);
////        }
//        dbObject.removeField("_class");
//        return dbObject;
//    }
//
//}
