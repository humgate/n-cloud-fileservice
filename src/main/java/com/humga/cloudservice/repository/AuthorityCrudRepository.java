package com.humga.cloudservice.repository;

import com.humga.cloudservice.model.entity.Authority;
import com.humga.cloudservice.model.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AuthorityCrudRepository extends CrudRepository<Authority, Long> {
    List<Authority> findAuthorityByUser(User user);
}
