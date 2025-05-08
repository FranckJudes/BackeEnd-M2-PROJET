package com.harmony.harmoniservices.infrastructure.persistance.repository.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.harmony.harmoniservices.core.domain.entities.ProcessExecution;
import com.harmony.harmoniservices.core.ports.repository.ProcessExecutionRepository;
import com.harmony.harmoniservices.infrastructure.persistance.entitites.ProcessExecutionEntity;
import com.harmony.harmoniservices.infrastructure.persistance.mappers.ProcessExecutionMapper;
import com.harmony.harmoniservices.infrastructure.persistance.repository.jpa.ProcessExecutionRepositoryJpa;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProcessExecutionRepositoryImpl implements ProcessExecutionRepository {

    private final ProcessExecutionRepositoryJpa jpaRepository;
    private final ProcessExecutionMapper mapper;

    @Override
    public ProcessExecution save(ProcessExecution processExecution) {
        ProcessExecutionEntity entity = mapper.toEntity(processExecution);
        entity = jpaRepository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<ProcessExecution> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<ProcessExecution> findByProcessInstanceId(Long processInstanceId) {
        return jpaRepository.findByProcessInstanceId(processInstanceId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
} 