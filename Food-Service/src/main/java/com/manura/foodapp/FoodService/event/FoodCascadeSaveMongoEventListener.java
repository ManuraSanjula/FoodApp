package com.manura.foodapp.FoodService.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import com.manura.foodapp.FoodService.entity.FoodEntity;

@Component
public class FoodCascadeSaveMongoEventListener extends AbstractMongoEventListener<Object> {
    @Autowired
    private ReactiveMongoOperations mongoOperations;

    @Override
    public void onBeforeConvert(final BeforeConvertEvent<Object> event) {
        final Object source = event.getSource();
        if ((source instanceof FoodEntity) && (((FoodEntity) source).getId() != null)) {
            mongoOperations.save(((FoodEntity) source).getId());
        }
    }
}
