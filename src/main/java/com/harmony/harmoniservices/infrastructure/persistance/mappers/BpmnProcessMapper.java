package com.harmony.harmoniservices.infrastructure.persistance.mappers;

import org.springframework.stereotype.Component;

import com.harmony.harmoniservices.core.domain.entities.BpmnProcess;

@Component
public class BpmnProcessMapper {
    
    public com.harmony.harmoniservices.infrastructure.persistance.entitites.bpmn.BpmnProcess toEntity(BpmnProcess process) {
        if (process == null) {
            return null;
        }
        
        return com.harmony.harmoniservices.infrastructure.persistance.entitites.bpmn.BpmnProcess.builder()
            .id(process.getId())
            .name(process.getName())
            .isExecutable(process.getIsExecutable())
            .description(process.getDescription())
            .keywords(process.getKeywords())
            .imagePaths(process.getImagePaths())
            .filePaths(process.getFilePaths())
            .createdAt(process.getCreatedAt())
            .updatedAt(process.getUpdatedAt())
            .build();
    }
    
    public BpmnProcess toDomain(com.harmony.harmoniservices.infrastructure.persistance.entitites.bpmn.BpmnProcess entity) {
        if (entity == null) {
            return null;
        }
        
        return BpmnProcess.builder()
            .id(entity.getId())
            .name(entity.getName())
            .isExecutable(entity.getIsExecutable())
            .description(entity.getDescription())
            .keywords(entity.getKeywords())
            .imagePaths(entity.getImagePaths())
            .filePaths(entity.getFilePaths())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
} 