package com.manura.foodapp.FoodService.event;

import java.lang.reflect.Field;

import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.util.ReflectionUtils;

import com.manura.foodapp.FoodService.anotation.CascadeSave;

public class CascadeCallback implements ReflectionUtils.FieldCallback {

    private Object source;
    private ReactiveMongoOperations mongoOperations;

    CascadeCallback(final Object source, final ReactiveMongoOperations mongoOperations) {
        this.source = source;
        this.setMongoOperations(mongoOperations);
    }

    @Override
    public void doWith(final Field field) throws IllegalArgumentException, IllegalAccessException {
        ReflectionUtils.makeAccessible(field);

        if (field.isAnnotationPresent(DBRef.class) && field.isAnnotationPresent(CascadeSave.class)) {
            final Object fieldValue = field.get(getSource());

            if (fieldValue != null) {
                final FieldCallback callback = new FieldCallback();

                ReflectionUtils.doWithFields(fieldValue.getClass(), callback);

                getMongoOperations().save(fieldValue);
            }
        }

    }

    private Object getSource() {
        return source;
    }

    public void setSource(final Object source) {
        this.source = source;
    }

    private ReactiveMongoOperations getMongoOperations() {
        return mongoOperations;
    }

    private void setMongoOperations(final ReactiveMongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }
}
