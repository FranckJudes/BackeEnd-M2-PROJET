package com.harmony.harmoniservices.infrastructure.persistance.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.harmony.harmoniservices.infrastructure.persistance.entitites.bpmn.BpmnProcess;

public interface BpmnProcessRepositoryJpa extends JpaRepository<BpmnProcess, String> {
    
} 