package com.manura.foodapp.UserService.repository;

import com.manura.foodapp.UserService.entity.AuthorityEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepo extends CrudRepository<AuthorityEntity, Long> {
   AuthorityEntity findByName(String name);
}
