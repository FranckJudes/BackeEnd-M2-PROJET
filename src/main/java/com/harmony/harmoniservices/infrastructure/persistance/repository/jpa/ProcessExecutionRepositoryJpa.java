package com.harmony.harmoniservices.infrastructure.persistance.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.harmony.harmoniservices.infrastructure.persistance.entitites.ProcessExecutionEntity;

public interface ProcessExecutionRepositoryJpa extends JpaRepository<ProcessExecutionEntity, Long> {
    
    List<ProcessExecutionEntity> findByProcessInstanceId(Long processInstanceId);
} 