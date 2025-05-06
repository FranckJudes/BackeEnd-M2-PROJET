package com.harmony.harmoniservices.core.ports.repository;

import java.util.List;
import java.util.Optional;

import com.harmony.harmoniservices.core.domain.entities.User;

public interface UserRepository {
    User save(User userEntity);
    User update(User userEntity);
    Optional<User> findById(Long id);
    void deleteById(Long id);
    List<User> findAll();
    boolean existsById(Long id);
}