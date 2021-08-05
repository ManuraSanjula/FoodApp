package com.manura.foodapp.FoodService.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Component
public class CascadeSaveMongoEventListener extends AbstractMongoEventListener<Object> {

    @Autowired
    private ReactiveMongoOperations mongoOperations;

    @Override
    public void onBeforeConvert(final BeforeConvertEvent<Object> event) {
        final Object source = event.getSource();
        ReflectionUtils.doWithFields(source.getClass(), new CascadeCallback(source, mongoOperations));
    }
}