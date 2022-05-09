package com.humga.cloudservice.repository;

import com.humga.cloudservice.entity.File;
import com.humga.cloudservice.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCrudRepository extends CrudRepository<User, Long> {
    Optional<User> getUserById(long id);
}
