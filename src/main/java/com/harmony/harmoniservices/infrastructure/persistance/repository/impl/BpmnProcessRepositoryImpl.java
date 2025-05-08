package com.harmony.harmoniservices.infrastructure.persistance.repository.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.harmony.harmoniservices.core.domain.entities.BpmnProcess;
import com.harmony.harmoniservices.core.ports.repository.BpmnProcessRepository;
import com.harmony.harmoniservices.infrastructure.persistance.mappers.BpmnProcessMapper;
import com.harmony.harmoniservices.infrastructure.persistance.repository.jpa.BpmnProcessRepositoryJpa;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BpmnProcessRepositoryImpl implements BpmnProcessRepository {

    private final BpmnProcessRepositoryJpa jpaRepository;
    private final BpmnProcessMapper mapper;

    @Override
    public BpmnProcess save(BpmnProcess process) {
        com.harmony.harmoniservices.infrastructure.persistance.entitites.bpmn.BpmnProcess entity = mapper.toEntity(process);
        entity = jpaRepository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<BpmnProcess> findById(String id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<BpmnProcess> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }
} 