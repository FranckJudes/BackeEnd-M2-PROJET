package com.harmony.harmoniservices.core.ports.repository;

import java.util.List;
import java.util.Optional;

import com.harmony.harmoniservices.core.domain.entities.ProcessExecution;

/**
 * Repository pour les exécutions de tâches de processus
 */
public interface ProcessExecutionRepository {
    
    /**
     * Sauvegarde une exécution de tâche
     * @param processExecution Exécution à sauvegarder
     * @return Exécution sauvegardée
     */
    ProcessExecution save(ProcessExecution processExecution);
    
    /**
     * Trouve une exécution par son ID
     * @param id ID de l'exécution
     * @return Exécution si trouvée, sinon empty
     */
    Optional<ProcessExecution> findById(Long id);
    
    /**
     * Trouve toutes les exécutions pour une instance de processus
     * @param processInstanceId ID de l'instance de processus
     * @return Liste des exécutions
     */
    List<ProcessExecution> findByProcessInstanceId(Long processInstanceId);
} 