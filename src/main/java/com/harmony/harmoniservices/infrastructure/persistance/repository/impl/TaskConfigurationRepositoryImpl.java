package com.harmony.harmoniservices.infrastructure.persistance.repository.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.harmony.harmoniservices.core.domain.entities.TaskConfiguration;
import com.harmony.harmoniservices.core.ports.repository.TaskConfigurationRepository;
import com.harmony.harmoniservices.infrastructure.persistance.entitites.TaskConfigurationEntity;
import com.harmony.harmoniservices.infrastructure.persistance.mappers.TaskConfigurationMapper;
import com.harmony.harmoniservices.infrastructure.persistance.repository.jpa.TaskConfigurationRepositoryJpa;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TaskConfigurationRepositoryImpl implements TaskConfigurationRepository {

    private final TaskConfigurationRepositoryJpa jpaRepository;
    private final TaskConfigurationMapper mapper;

    @Override
    public TaskConfiguration save(TaskConfiguration taskConfiguration) {
        TaskConfigurationEntity entity = mapper.toEntity(taskConfiguration);
        entity = jpaRepository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<TaskConfiguration> findByTaskId(String taskId) {
        return jpaRepository.findByTaskId(taskId)
                .map(mapper::toDomain);
    }

    @Override
    public List<TaskConfiguration> findByTaskIdIn(List<String> taskIds) {
        return jpaRepository.findByTaskIdIn(taskIds)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByTaskId(String taskId) {
        jpaRepository.deleteByTaskId(taskId);
    }
} 