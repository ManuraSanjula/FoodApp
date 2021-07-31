package com.manura.foodapp.repository;

import com.manura.foodapp.entity.RoleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends CrudRepository<RoleEntity, Long> {
   RoleEntity findByRole(String name);
}
