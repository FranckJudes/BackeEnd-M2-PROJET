package com.harmony.harmoniservices.infrastructure.persistance.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.harmony.harmoniservices.core.domain.entities.GroupeUtilisateur;
import com.harmony.harmoniservices.core.domain.entities.TaskConfiguration;
import com.harmony.harmoniservices.core.domain.entities.User;
import com.harmony.harmoniservices.infrastructure.persistance.entitites.GroupeEntity;
import com.harmony.harmoniservices.infrastructure.persistance.entitites.TaskConfigurationEntity;
import com.harmony.harmoniservices.infrastructure.persistance.entitites.UserEntity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TaskConfigurationMapper {
    
    private final UserMapper userMapper;
    
    public TaskConfigurationEntity toEntity(TaskConfiguration configuration) {
        if (configuration == null) {
            return null;
        }
        
        List<GroupeEntity> groupesEntities = null;
        if (configuration.getAuthorizedGroups() != null) {
            groupesEntities = configuration.getAuthorizedGroups().stream()
                .map(GroupeMapper::toEntity)
                .collect(Collectors.toList());
        }
        
        UserEntity assignedUserEntity = null;
        if (configuration.getAssignedUser() != null) {
            assignedUserEntity = userMapper.toEntity(configuration.getAssignedUser());
        }
        
        return TaskConfigurationEntity.builder()
            .id(configuration.getId())
            .taskId(configuration.getTaskId())
            .requiredRoles(configuration.getRequiredRoles())
            .authorizedGroups(groupesEntities)
            .needSupervisorValidation(configuration.getNeedSupervisorValidation())
            .startDate(configuration.getStartDate())
            .endDate(configuration.getEndDate())
            .maxDurationInMinutes(configuration.getMaxDurationInMinutes())
            .cronExpression(configuration.getCronExpression())
            .isRecurring(configuration.getIsRecurring())
            .minRequiredResources(configuration.getMinRequiredResources())
            .maxRequiredResources(configuration.getMaxRequiredResources())
            .requiredSkills(configuration.getRequiredSkills())
            .priority(configuration.getPriority())
            .sendNotificationOnStart(configuration.getSendNotificationOnStart())
            .sendNotificationOnCompletion(configuration.getSendNotificationOnCompletion())
            .sendNotificationOnDelay(configuration.getSendNotificationOnDelay())
            .notificationRecipients(configuration.getNotificationRecipients())
            .notificationTemplate(configuration.getNotificationTemplate())
            .executionStatus(configuration.getExecutionStatus())
            .lastExecutionDate(configuration.getLastExecutionDate())
            .lastExecutionResult(configuration.getLastExecutionResult())
            .assignedUser(assignedUserEntity)
            .description(configuration.getDescription())
            .createdAt(configuration.getCreatedAt())
            .updatedAt(configuration.getUpdatedAt())
            .build();
    }
    
    public TaskConfiguration toDomain(TaskConfigurationEntity entity) {
        if (entity == null) {
            return null;
        }
        
        List<GroupeUtilisateur> groupes = null;
        if (entity.getAuthorizedGroups() != null) {
            groupes = entity.getAuthorizedGroups().stream()
                .map(GroupeMapper::toDomain)
                .collect(Collectors.toList());
        }
        
        User assignedUser = null;
        if (entity.getAssignedUser() != null) {
            assignedUser = userMapper.toDomain(entity.getAssignedUser());
        }
        
        return TaskConfiguration.builder()
            .id(entity.getId())
            .taskId(entity.getTaskId())
            .requiredRoles(entity.getRequiredRoles())
            .authorizedGroups(groupes)
            .needSupervisorValidation(entity.getNeedSupervisorValidation())
            .startDate(entity.getStartDate())
            .endDate(entity.getEndDate())
            .maxDurationInMinutes(entity.getMaxDurationInMinutes())
            .cronExpression(entity.getCronExpression())
            .isRecurring(entity.getIsRecurring())
            .minRequiredResources(entity.getMinRequiredResources())
            .maxRequiredResources(entity.getMaxRequiredResources())
            .requiredSkills(entity.getRequiredSkills())
            .priority(entity.getPriority())
            .sendNotificationOnStart(entity.getSendNotificationOnStart())
            .sendNotificationOnCompletion(entity.getSendNotificationOnCompletion())
            .sendNotificationOnDelay(entity.getSendNotificationOnDelay())
            .notificationRecipients(entity.getNotificationRecipients())
            .notificationTemplate(entity.getNotificationTemplate())
            .executionStatus(entity.getExecutionStatus())
            .lastExecutionDate(entity.getLastExecutionDate())
            .lastExecutionResult(entity.getLastExecutionResult())
            .assignedUser(assignedUser)
            .description(entity.getDescription())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
} 