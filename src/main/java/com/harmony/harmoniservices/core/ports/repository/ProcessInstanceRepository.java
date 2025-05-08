package com.harmony.harmoniservices.core.ports.repository;

import java.util.List;
import java.util.Optional;

import com.harmony.harmoniservices.core.domain.entities.ProcessInstance;

/**
 * Repository pour les instances de processus
 */
public interface ProcessInstanceRepository {
    
    /**
     * Sauvegarde une instance de processus
     * @param processInstance Instance à sauvegarder
     * @return Instance sauvegardée
     */
    ProcessInstance save(ProcessInstance processInstance);
    
    /**
     * Trouve une instance de processus par son ID
     * @param id ID de l'instance
     * @return Instance si trouvée, sinon empty
     */
    Optional<ProcessInstance> findById(Long id);
    
    /**
     * Trouve toutes les instances de processus actives pour un processus donné
     * @param processId ID du processus
     * @return Liste des instances actives
     */
    List<ProcessInstance> findActiveInstancesByProcessId(String processId);
    
    /**
     * Trouve toutes les instances de processus actives avec des tâches assignées à un utilisateur
     * @param userId ID de l'utilisateur
     * @return Liste des instances avec des tâches pour l'utilisateur
     */
    List<ProcessInstance> findActiveInstancesWithUserTasks(Long userId);
    
    /**
     * Trouve toutes les instances de processus actives avec des tâches disponibles pour un groupe
     * @param groupId ID du groupe
     * @return Liste des instances avec des tâches pour le groupe
     */
    List<ProcessInstance> findActiveInstancesWithGroupTasks(Long groupId);
} 