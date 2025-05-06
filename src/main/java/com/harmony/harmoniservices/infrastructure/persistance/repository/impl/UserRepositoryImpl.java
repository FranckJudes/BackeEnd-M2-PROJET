package com.harmony.harmoniservices.infrastructure.persistance.repository.impl;

import java.util.List;
import java.util.Optional;

import com.harmony.harmoniservices.core.domain.entities.User;
import com.harmony.harmoniservices.core.ports.repository.UserRepository;
import com.harmony.harmoniservices.infrastructure.persistance.entitites.UserEntity;
import com.harmony.harmoniservices.infrastructure.persistance.mappers.UserMapper;
import com.harmony.harmoniservices.infrastructure.persistance.repository.jpa.UserRepositoryJpa;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl  implements UserRepository{

     private final UserRepositoryJpa jpaRepository;
     private final UserMapper mapper;

    UserRepositoryImpl(UserRepositoryJpa jpaRepository) {
        this.jpaRepository = jpaRepository;
        this.mapper = new UserMapper();
    }

    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        entity = jpaRepository.save(entity);
        return mapper.toDomain(entity);
    }


    @Override
    public Optional<User> findById(Long id) {
        Optional<UserEntity> entity = jpaRepository.findById(id);
        return entity.map(mapper::toDomain);
        
    }

    @Override
    public void deleteById(Long userId) {
        jpaRepository.deleteById(userId);
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsById(Long id) {
        throw new UnsupportedOperationException("Unimplemented method 'existsById'");
    }

    @Override
    public User update(User userEntity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

}
