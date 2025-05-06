package com.harmony.harmoniservices.core.ports.cases;

import java.util.List;
import java.util.Optional;

import com.harmony.harmoniservices.core.domain.entities.User;

public interface UserService {

    User createUser(User user);
    User updateUser(User user);
    void deleteUser(Long userId);
    Optional<User> findUser(Long id);
    List<User> getAll();
}