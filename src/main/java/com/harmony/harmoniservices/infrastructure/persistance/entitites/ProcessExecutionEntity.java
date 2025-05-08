package com.harmony.harmoniservices.infrastructure.persistance.entitites;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entity pour les exécutions de tâches dans un processus BPMN
 */
@Entity
@Table(name = "process_executions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ProcessExecutionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Références
    @Column(name = "task_id")
    private String taskId;
    
    @Column(name = "task_name")
    private String taskName;
    
    @Column(name = "process_instance_id")
    private Long processInstanceId;
    
    // Informations d'exécution
    @Column(name = "status")
    private String status;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "duration_millis")
    private Long durationInMillis;
    
    // Acteur
    @ManyToOne
    @JoinColumn(name = "executor_id")
    private UserEntity executor;
    
    // Résultat
    @Column(name = "result", columnDefinition = "TEXT")
    private String result;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    // Variables
    @Column(name = "input_variables", columnDefinition = "TEXT")
    private String inputVariables;
    
    @Column(name = "output_variables", columnDefinition = "TEXT")
    private String outputVariables;
    
    // Métadonnées
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
} 