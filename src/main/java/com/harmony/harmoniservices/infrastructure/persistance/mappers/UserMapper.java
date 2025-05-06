package com.harmony.harmoniservices.infrastructure.persistance.mappers;

import org.springframework.stereotype.Component;

import com.harmony.harmoniservices.core.domain.entities.User;
import com.harmony.harmoniservices.infrastructure.persistance.entitites.UserEntity;

@Component 
public class UserMapper {

    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }
        return UserEntity.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}