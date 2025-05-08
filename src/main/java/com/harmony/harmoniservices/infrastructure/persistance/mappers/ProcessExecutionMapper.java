package com.harmony.harmoniservices.infrastructure.persistance.mappers;

import org.springframework.stereotype.Component;

import com.harmony.harmoniservices.core.domain.entities.ProcessExecution;
import com.harmony.harmoniservices.core.domain.entities.User;
import com.harmony.harmoniservices.infrastructure.persistance.entitites.ProcessExecutionEntity;
import com.harmony.harmoniservices.infrastructure.persistance.entitites.UserEntity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProcessExecutionMapper {
    
    private final UserMapper userMapper;
    
    public ProcessExecutionEntity toEntity(ProcessExecution execution) {
        if (execution == null) {
            return null;
        }
        
        UserEntity executorEntity = null;
        if (execution.getExecutor() != null) {
            executorEntity = userMapper.toEntity(execution.getExecutor());
        }
        
        return ProcessExecutionEntity.builder()
            .id(execution.getId())
            .taskId(execution.getTaskId())
            .taskName(execution.getTaskName())
            .processInstanceId(execution.getProcessInstanceId())
            .status(execution.getStatus())
            .startTime(execution.getStartTime())
            .endTime(execution.getEndTime())
            .durationInMillis(execution.getDurationInMillis())
            .executor(executorEntity)
            .result(execution.getResult())
            .errorMessage(execution.getErrorMessage())
            .inputVariables(execution.getInputVariables())
            .outputVariables(execution.getOutputVariables())
            .createdAt(execution.getCreatedAt())
            .build();
    }
    
    public ProcessExecution toDomain(ProcessExecutionEntity entity) {
        if (entity == null) {
            return null;
        }
        
        User executor = null;
        if (entity.getExecutor() != null) {
            executor = userMapper.toDomain(entity.getExecutor());
        }
        
        return ProcessExecution.builder()
            .id(entity.getId())
            .taskId(entity.getTaskId())
            .taskName(entity.getTaskName())
            .processInstanceId(entity.getProcessInstanceId())
            .status(entity.getStatus())
            .startTime(entity.getStartTime())
            .endTime(entity.getEndTime())
            .durationInMillis(entity.getDurationInMillis())
            .executor(executor)
            .result(entity.getResult())
            .errorMessage(entity.getErrorMessage())
            .inputVariables(entity.getInputVariables())
            .outputVariables(entity.getOutputVariables())
            .createdAt(entity.getCreatedAt())
            .build();
    }
} 