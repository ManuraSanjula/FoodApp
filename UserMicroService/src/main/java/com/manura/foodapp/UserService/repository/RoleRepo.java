package com.manura.foodapp.UserService.repository;

import com.manura.foodapp.UserService.entity.RoleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends CrudRepository<RoleEntity, Long> {
   RoleEntity findByRole(String name);
}
