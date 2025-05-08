package com.harmony.harmoniservices.infrastructure.persistance.mappers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harmony.harmoniservices.core.domain.entities.ProcessExecution;
import com.harmony.harmoniservices.core.domain.entities.ProcessInstance;
import com.harmony.harmoniservices.core.domain.entities.User;
import com.harmony.harmoniservices.infrastructure.persistance.entitites.ProcessExecutionEntity;
import com.harmony.harmoniservices.infrastructure.persistance.entitites.ProcessInstanceEntity;
import com.harmony.harmoniservices.infrastructure.persistance.entitites.UserEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessInstanceMapper {
    
    private final UserMapper userMapper;
    private final ProcessExecutionMapper processExecutionMapper;
    private final BpmnProcessMapper bpmnProcessMapper;
    private final ObjectMapper objectMapper;
    
    public ProcessInstanceEntity toEntity(ProcessInstance instance) {
        if (instance == null) {
            return null;
        }
        
        UserEntity initiatorEntity = null;
        if (instance.getInitiator() != null) {
            initiatorEntity = userMapper.toEntity(instance.getInitiator());
        }
        
        List<ProcessExecutionEntity> executionHistoryEntities = null;
        if (instance.getExecutionHistory() != null) {
            executionHistoryEntities = instance.getExecutionHistory().stream()
                .map(processExecutionMapper::toEntity)
                .collect(Collectors.toList());
        }
        
        String processVariablesJson = null;
        if (instance.getProcessVariables() != null) {
            try {
                processVariablesJson = objectMapper.writeValueAsString(instance.getProcessVariables());
            } catch (JsonProcessingException e) {
                log.error("Erreur lors de la conversion des variables de processus en JSON", e);
                processVariablesJson = "{}";
            }
        }
        
        return ProcessInstanceEntity.builder()
            .id(instance.getId())
            .process(bpmnProcessMapper.toEntity(instance.getProcess()))
            .status(instance.getStatus())
            .startTime(instance.getStartTime())
            .endTime(instance.getEndTime())
            .currentTaskId(instance.getCurrentTaskId())
            .executionHistory(executionHistoryEntities)
            .processVariablesJson(processVariablesJson)
            .initiator(initiatorEntity)
            .businessKey(instance.getBusinessKey())
            .description(instance.getDescription())
            .createdAt(instance.getCreatedAt())
            .updatedAt(instance.getUpdatedAt())
            .build();
    }
    
    public ProcessInstance toDomain(ProcessInstanceEntity entity) {
        if (entity == null) {
            return null;
        }
        
        User initiator = null;
        if (entity.getInitiator() != null) {
            initiator = userMapper.toDomain(entity.getInitiator());
        }
        
        List<ProcessExecution> executionHistory = null;
        if (entity.getExecutionHistory() != null) {
            executionHistory = entity.getExecutionHistory().stream()
                .map(processExecutionMapper::toDomain)
                .collect(Collectors.toList());
        }
        
        Map<String, Object> processVariables = new HashMap<>();
        if (entity.getProcessVariablesJson() != null && !entity.getProcessVariablesJson().isEmpty()) {
            try {
                processVariables = objectMapper.readValue(
                    entity.getProcessVariablesJson(),
                    new TypeReference<Map<String, Object>>() {}
                );
            } catch (JsonProcessingException e) {
                log.error("Erreur lors de la conversion du JSON en variables de processus", e);
            }
        }
        
        return ProcessInstance.builder()
            .id(entity.getId())
            .process(bpmnProcessMapper.toDomain(entity.getProcess()))
            .status(entity.getStatus())
            .startTime(entity.getStartTime())
            .endTime(entity.getEndTime())
            .currentTaskId(entity.getCurrentTaskId())
            .executionHistory(executionHistory)
            .processVariables(processVariables)
            .initiator(initiator)
            .businessKey(entity.getBusinessKey())
            .description(entity.getDescription())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
} 