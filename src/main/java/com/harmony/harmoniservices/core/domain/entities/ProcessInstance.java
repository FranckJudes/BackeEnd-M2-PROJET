package com.harmony.harmoniservices.core.domain.entities;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Représente une instance de processus BPMN en cours d'exécution
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessInstance {
    private Long id;
    
    // Référence au processus BPMN
    private BpmnProcess process;
    
    // Informations d'état
    private String status; // "ACTIVE", "COMPLETED", "SUSPENDED", "TERMINATED", "FAILED"
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String currentTaskId; // ID de la tâche active actuelle
    
    // Historique d'exécution
    private List<ProcessExecution> executionHistory;
    
    // Variables du processus
    private Map<String, Object> processVariables;
    
    // Informations sur le démarreur
    private User initiator;
    
    // Métadonnées
    private String businessKey; // Identifiant métier (référence externe)
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 