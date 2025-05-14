package com.harmony.harmoniservices.core.domain.services;

import java.util.Map;

import com.harmony.harmoniservices.core.domain.entities.Event;
import com.harmony.harmoniservices.core.domain.entities.ProcessInstance;

/**
 * Interface pour la gestion des événements BPMN
 */
public interface EventHandler {
    
    /**
     * Enregistre un événement de type timer pour une instance de processus
     * @param processInstance Instance de processus
     * @param event Événement BPMN
     * @param variables Variables du processus
     * @return ID de l'événement enregistré
     */
    String scheduleTimerEvent(ProcessInstance processInstance, Event event, Map<String, Object> variables);
    
    /**
     * Annule un événement timer programmé
     * @param eventId ID de l'événement
     * @return true si l'événement a été annulé avec succès, false sinon
     */
    boolean cancelTimerEvent(String eventId);
    
    /**
     * Publie un message pour les événements de type message
     * @param messageName Nom du message
     * @param correlationKey Clé de corrélation pour identifier le destinataire
     * @param variables Variables à transmettre
     */
    void publishMessage(String messageName, String correlationKey, Map<String, Object> variables);
    
    /**
     * Souscrit à un événement message pour une instance de processus
     * @param processInstance Instance de processus
     * @param event Événement BPMN de type message
     * @param correlationKey Clé de corrélation
     * @return ID de la souscription
     */
    String subscribeToMessage(ProcessInstance processInstance, Event event, String correlationKey);
    
    /**
     * Annule une souscription à un message
     * @param subscriptionId ID de la souscription
     * @return true si la souscription a été annulée avec succès, false sinon
     */
    boolean cancelMessageSubscription(String subscriptionId);
    
    /**
     * Publie un signal
     * @param signalName Nom du signal
     * @param variables Variables à transmettre
     */
    void publishSignal(String signalName, Map<String, Object> variables);
    
    /**
     * Souscrit à un événement signal pour une instance de processus
     * @param processInstance Instance de processus
     * @param event Événement BPMN de type signal
     * @return ID de la souscription
     */
    String subscribeToSignal(ProcessInstance processInstance, Event event);
    
    /**
     * Annule une souscription à un signal
     * @param subscriptionId ID de la souscription
     * @return true si la souscription a été annulée avec succès, false sinon
     */
    boolean cancelSignalSubscription(String subscriptionId);
    
    /**
     * Déclenche un événement pour une instance de processus
     * @param processInstanceId ID de l'instance de processus
     * @param eventId ID de l'événement BPMN
     * @param variables Variables à transmettre
     * @return true si l'événement a été déclenché avec succès, false sinon
     */
    boolean triggerEvent(Long processInstanceId, String eventId, Map<String, Object> variables);
} 