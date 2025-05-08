package com.harmony.harmoniservices.core.ports.repository;

import java.util.List;
import java.util.Optional;

import com.harmony.harmoniservices.core.domain.entities.TaskConfiguration;

/**
 * Repository pour les configurations des tâches
 */
public interface TaskConfigurationRepository {
    
    /**
     * Sauvegarde une configuration de tâche
     * @param taskConfiguration Configuration à sauvegarder
     * @return Configuration sauvegardée
     */
    TaskConfiguration save(TaskConfiguration taskConfiguration);
    
    /**
     * Trouve une configuration par l'ID de la tâche
     * @param taskId ID de la tâche
     * @return Configuration si trouvée, sinon empty
     */
    Optional<TaskConfiguration> findByTaskId(String taskId);
    
    /**
     * Trouve des configurations par une liste d'IDs de tâches
     * @param taskIds Liste d'IDs de tâches
     * @return Liste des configurations
     */
    List<TaskConfiguration> findByTaskIdIn(List<String> taskIds);
    
    /**
     * Supprime une configuration par l'ID de la tâche
     * @param taskId ID de la tâche
     */
    void deleteByTaskId(String taskId);
} 