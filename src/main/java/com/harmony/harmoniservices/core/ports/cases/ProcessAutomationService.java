package com.harmony.harmoniservices.core.ports.cases;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.harmony.harmoniservices.core.domain.entities.BpmnProcess;
import com.harmony.harmoniservices.core.domain.entities.ProcessInstance;
import com.harmony.harmoniservices.core.domain.entities.TaskConfiguration;
import com.harmony.harmoniservices.core.domain.entities.User;

/**
 * Service pour l'automatisation des processus BPMN
 */
public interface ProcessAutomationService {
    
    /**
     * Démarre une nouvelle instance de processus
     * @param processId ID du processus BPMN
     * @param initiator Utilisateur qui démarre le processus
     * @param variables Variables initiales du processus
     * @param businessKey Clé métier (identifiant externe)
     * @return Instance de processus créée
     */
    ProcessInstance startProcess(String processId, User initiator, Map<String, Object> variables, String businessKey);
    
    /**
     * Complète une tâche dans une instance de processus
     * @param processInstanceId ID de l'instance de processus
     * @param taskId ID de la tâche
     * @param user Utilisateur qui complète la tâche
     * @param variables Variables de sortie de la tâche
     * @return Instance de processus mise à jour
     */
    ProcessInstance completeTask(Long processInstanceId, String taskId, User user, Map<String, Object> variables);
    
    /**
     * Récupère les tâches assignées à un utilisateur
     * @param userId ID de l'utilisateur
     * @return Liste des instances de processus avec des tâches assignées à l'utilisateur
     */
    List<ProcessInstance> getUserTasks(Long userId);
    
    /**
     * Récupère les tâches disponibles pour un groupe
     * @param groupId ID du groupe
     * @return Liste des instances de processus avec des tâches disponibles pour le groupe
     */
    List<ProcessInstance> getGroupTasks(Long groupId);
    
    /**
     * Récupère une instance de processus
     * @param processInstanceId ID de l'instance de processus
     * @return Optional contenant l'instance de processus si elle existe
     */
    Optional<ProcessInstance> getProcessInstance(Long processInstanceId);
    
    /**
     * Récupère les instances actives d'un processus
     * @param processId ID du processus BPMN
     * @return Liste des instances actives du processus
     */
    List<ProcessInstance> getActiveProcessInstances(String processId);
    
    /**
     * Configure une tâche dans un processus
     * @param processId ID du processus BPMN
     * @param taskId ID de la tâche
     * @param configuration Configuration de la tâche
     * @return Configuration de la tâche créée ou mise à jour
     */
    TaskConfiguration configureTask(String processId, String taskId, TaskConfiguration configuration);
    
    /**
     * Récupère la configuration d'une tâche
     * @param processId ID du processus BPMN
     * @param taskId ID de la tâche
     * @return Optional contenant la configuration de la tâche si elle existe
     */
    Optional<TaskConfiguration> getTaskConfiguration(String processId, String taskId);
    
    /**
     * Récupère toutes les configurations des tâches d'un processus
     * @param processId ID du processus BPMN
     * @return Liste des configurations des tâches du processus
     */
    List<TaskConfiguration> getProcessTaskConfigurations(String processId);
    
    /**
     * Suspend une instance de processus
     * @param processInstanceId ID de l'instance de processus
     * @return Instance de processus mise à jour
     */
    ProcessInstance suspendProcess(Long processInstanceId);
    
    /**
     * Reprend une instance de processus suspendue
     * @param processInstanceId ID de l'instance de processus
     * @return Instance de processus mise à jour
     */
    ProcessInstance resumeProcess(Long processInstanceId);
    
    /**
     * Arrête une instance de processus
     * @param processInstanceId ID de l'instance de processus
     * @return Instance de processus mise à jour
     */
    ProcessInstance terminateProcess(Long processInstanceId);
    
    /**
     * Déploie un nouveau processus BPMN
     * @param processDefinition Définition du processus BPMN (XML)
     * @param name Nom du processus
     * @return Processus déployé
     */
    BpmnProcess deployProcess(String processDefinition, String name);
    
    /**
     * Déclenche un événement dans une instance de processus
     * @param processInstanceId ID de l'instance de processus
     * @param eventId ID de l'événement BPMN
     * @param variables Variables à transmettre à l'événement
     * @return true si l'événement a été déclenché avec succès, false sinon
     */
    boolean triggerEvent(Long processInstanceId, String eventId, Map<String, Object> variables);
} 