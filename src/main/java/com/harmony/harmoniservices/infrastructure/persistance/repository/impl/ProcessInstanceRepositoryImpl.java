package com.harmony.harmoniservices.infrastructure.persistance.repository.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.harmony.harmoniservices.core.domain.entities.ProcessInstance;
import com.harmony.harmoniservices.core.ports.repository.ProcessInstanceRepository;
import com.harmony.harmoniservices.infrastructure.persistance.entitites.ProcessInstanceEntity;
import com.harmony.harmoniservices.infrastructure.persistance.mappers.ProcessInstanceMapper;
import com.harmony.harmoniservices.infrastructure.persistance.repository.jpa.ProcessInstanceRepositoryJpa;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProcessInstanceRepositoryImpl implements ProcessInstanceRepository {

    private final ProcessInstanceRepositoryJpa jpaRepository;
    private final ProcessInstanceMapper mapper;

    @Override
    public ProcessInstance save(ProcessInstance processInstance) {
        ProcessInstanceEntity entity = mapper.toEntity(processInstance);
        entity = jpaRepository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<ProcessInstance> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<ProcessInstance> findActiveInstancesByProcessId(String processId) {
        return jpaRepository.findActiveInstancesByProcessId(processId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProcessInstance> findActiveInstancesWithUserTasks(Long userId) {
        return jpaRepository.findActiveInstancesWithUserTasks(userId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProcessInstance> findActiveInstancesWithGroupTasks(Long groupId) {
        return jpaRepository.findActiveInstancesWithGroupTasks(groupId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
} 