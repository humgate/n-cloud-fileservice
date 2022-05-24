package com.humga.cloudservice.repository;

import com.humga.cloudservice.model.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCrudRepository extends CrudRepository<User, Long> {
    Optional<User> getUserById(long id);
    Optional<User> getUserByName(String name);
    Optional<User> getUserByLogin(String login);
    List<User> findAll();
}
