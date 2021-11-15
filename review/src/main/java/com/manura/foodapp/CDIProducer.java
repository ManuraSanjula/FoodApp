
package com.manura.foodapp;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class CDIProducer {

    @Produces
    @PersistenceContext
    EntityManager entityManager;
}
