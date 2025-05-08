package com.harmony.harmoniservices.infrastructure.persistance.entitites;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.harmony.harmoniservices.infrastructure.persistance.entitites.bpmn.BpmnProcess;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity pour les instances de processus BPMN
 */
@Entity
@Table(name = "process_instances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ProcessInstanceEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Référence au processus BPMN
    @ManyToOne
    @JoinColumn(name = "process_id", nullable = false)
    private BpmnProcess process;
    
    // Informations d'état
    @Column(name = "status", nullable = false)
    private String status;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "current_task_id")
    private String currentTaskId;
    
    // Historique d'exécution
    @OneToMany(mappedBy = "processInstanceId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcessExecutionEntity> executionHistory;
    
    // Variables du processus (stockées sous forme JSON)
    @Column(name = "process_variables", columnDefinition = "TEXT")
    private String processVariablesJson;
    
    // Informations sur le démarreur
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private UserEntity initiator;
    
    // Métadonnées
    @Column(name = "business_key")
    private String businessKey;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
} 