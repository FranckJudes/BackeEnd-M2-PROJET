package com.harmony.harmoniservices.core.domain.entities;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Représente l'exécution d'une tâche dans une instance de processus
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessExecution {
    private Long id;
    
    // Références
    private String taskId;
    private String taskName;
    private Long processInstanceId;
    
    // Informations d'exécution
    private String status; // "COMPLETED", "FAILED", "SKIPPED", "CANCELLED"
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationInMillis;
    
    // Acteur
    private User executor;
    
    // Résultat
    private String result;
    private String errorMessage;
    
    // Variables
    private String inputVariables;  // JSON string
    private String outputVariables; // JSON string
    
    // Métadonnées
    private LocalDateTime createdAt;
} 