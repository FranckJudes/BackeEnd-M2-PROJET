package com.harmony.harmoniservices.core.ports.repository;

import java.util.List;
import java.util.Optional;

import com.harmony.harmoniservices.core.domain.entities.BpmnProcess;

/**
 * Repository pour les processus BPMN
 */
public interface BpmnProcessRepository {
    
    /**
     * Sauvegarde un processus BPMN
     * @param process Processus à sauvegarder
     * @return Processus sauvegardé
     */
    BpmnProcess save(BpmnProcess process);
    
    /**
     * Trouve un processus BPMN par son ID
     * @param id ID du processus
     * @return Processus si trouvé, sinon empty
     */
    Optional<BpmnProcess> findById(String id);
    
    /**
     * Trouve tous les processus BPMN
     * @return Liste des processus
     */
    List<BpmnProcess> findAll();
    
    /**
     * Supprime un processus BPMN par son ID
     * @param id ID du processus
     */
    void deleteById(String id);
} 