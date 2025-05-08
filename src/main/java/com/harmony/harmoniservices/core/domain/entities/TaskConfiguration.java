package com.harmony.harmoniservices.core.domain.entities;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Configuration d'une tâche dans un processus BPMN
 * Contient les paramètres d'habilitation, de planification, de ressources et de notification
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskConfiguration {
    private Long id;
    
    // Référence à la tâche BPMN
    private String taskId;
    
    // Paramètres d'habilitation
    private List<String> requiredRoles;
    private List<GroupeUtilisateur> authorizedGroups;
    private Boolean needSupervisorValidation;
    
    // Paramètres de planification
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer maxDurationInMinutes;
    private String cronExpression;
    private Boolean isRecurring;
    
    // Paramètres de ressources
    private Integer minRequiredResources;
    private Integer maxRequiredResources;
    private List<String> requiredSkills;
    private Integer priority; // 1-10, priorité de la tâche
    
    // Paramètres de notification
    private Boolean sendNotificationOnStart;
    private Boolean sendNotificationOnCompletion;
    private Boolean sendNotificationOnDelay;
    private List<String> notificationRecipients;
    private String notificationTemplate;
    
    // Statut d'exécution
    private String executionStatus; // "NOT_STARTED", "IN_PROGRESS", "COMPLETED", "FAILED", "SUSPENDED"
    
    // Données d'exécution
    private LocalDateTime lastExecutionDate;
    private String lastExecutionResult;
    private User assignedUser;
    
    // Métadonnées
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 