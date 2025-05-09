package com.harmony.harmoniservices.infrastructure.persistance.repository.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.harmony.harmoniservices.infrastructure.persistance.entitites.TaskConfigurationEntity;

public interface TaskConfigurationRepositoryJpa extends JpaRepository<TaskConfigurationEntity, Long> {
    
    Optional<TaskConfigurationEntity> findByTaskId(String taskId);
    
    List<TaskConfigurationEntity> findByTaskIdIn(List<String> taskIds);
    
    void deleteByTaskId(String taskId);
} 