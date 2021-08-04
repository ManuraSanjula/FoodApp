package com.manura.foodapp.FoodService.entity.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;

import com.manura.foodapp.FoodService.entity.FoodEntity;

public class FoodCascadeSaveMongoEventListener extends AbstractMongoEventListener<Object> {
    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public void onBeforeConvert(final BeforeConvertEvent<Object> event) {
        final Object source = event.getSource();
        if ((source instanceof FoodEntity) && (((FoodEntity) source).getId() != null)) {
            mongoOperations.save(((FoodEntity) source).getId());
        }
    }
}