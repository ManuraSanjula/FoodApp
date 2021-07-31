package com.manura.foodapp.repository;

import com.manura.foodapp.entity.AuthorityEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepo extends CrudRepository<AuthorityEntity, Long> {
   AuthorityEntity findByName(String name);
}
