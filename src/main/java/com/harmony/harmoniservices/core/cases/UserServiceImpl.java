package com.harmony.harmoniservices.core.cases;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.harmony.harmoniservices.core.domain.entities.User;
import com.harmony.harmoniservices.core.ports.cases.UserService;
import com.harmony.harmoniservices.core.ports.repository.UserRepository;
import com.harmony.harmoniservices.infrastructure.persistance.mappers.UserMapper;



@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
            
    public UserServiceImpl(UserMapper userMapper, UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new NoSuchElementException("Erreur d'insertion de l'utilisateur");
        }
    }

    @Override
    public User updateUser(User user) {
        try {
            return userRepository.update(user);
        } catch (Exception e) {
            throw new NoSuchElementException("Erreur d'insertion de l'utilisateur");
        }
    }

    @Override
    public void deleteUser(Long userId) {
       
        try {
            userRepository.deleteById(userId);
        } catch (Exception e) {
            throw new NoSuchElementException("Erreur d'insertion de l'utilisateur");
        }
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findUser(Long id) {
       try {
         return userRepository.findById(id);
       } catch (Exception e) {
            throw new NoSuchElementException("User with id " + id + " does not exist.");
       }
    }
}