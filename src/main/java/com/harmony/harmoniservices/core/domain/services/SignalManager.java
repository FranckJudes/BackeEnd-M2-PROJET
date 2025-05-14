package com.harmony.harmoniservices.core.domain.services;

import java.util.List;
import java.util.Map;

import com.harmony.harmoniservices.core.domain.events.SignalEvent;
import com.harmony.harmoniservices.core.domain.events.SignalSubscription;

/**
 * Service de gestion des signaux BPMN
 */
public interface SignalManager {
    
    /**
     * Publie un signal dans le système
     * 
     * @param signalName Nom du signal
     * @param variables Variables associées au signal
     * @param sourceProcessInstanceId ID de l'instance de processus qui publie le signal (peut être null)
     * @return L'événement créé
     */
    SignalEvent publishSignal(String signalName, Map<String, Object> variables, String sourceProcessInstanceId);
    
    /**
     * Surcharge pour publier un signal sans spécifier la source
     * 
     * @param signalName Nom du signal
     * @param variables Variables associées au signal
     * @return L'événement créé
     */
    default SignalEvent publishSignal(String signalName, Map<String, Object> variables) {
        return publishSignal(signalName, variables, null);
    }
    
    /**
     * Crée une souscription à un signal
     * 
     * @param processInstanceId ID de l'instance de processus qui souscrit
     * @param eventId ID de l'événement BPMN qui attend le signal
     * @param signalName Nom du signal à attendre
     * @return La souscription créée
     */
    SignalSubscription createSubscription(Long processInstanceId, String eventId, String signalName);
    
    /**
     * Annule une souscription à un signal
     * 
     * @param subscriptionId ID de la souscription
     * @return true si la souscription a été annulée avec succès, false sinon
     */
    boolean cancelSubscription(String subscriptionId);
    
    /**
     * Recherche les souscriptions actives pour un signal donné
     * 
     * @param signalName Nom du signal
     * @return Liste des souscriptions correspondantes
     */
    List<SignalSubscription> findActiveSubscriptions(String signalName);
    
    /**
     * Recherche une souscription par son ID
     * 
     * @param subscriptionId ID de la souscription
     * @return La souscription ou null si non trouvée
     */
    SignalSubscription getSubscription(String subscriptionId);
    
    /**
     * Récupère toutes les souscriptions actives pour une instance de processus
     * 
     * @param processInstanceId ID de l'instance de processus
     * @return Liste des souscriptions actives
     */
    List<SignalSubscription> getActiveSubscriptionsForProcess(Long processInstanceId);
    
    /**
     * Désactive toutes les souscriptions pour une instance de processus
     * 
     * @param processInstanceId ID de l'instance de processus
     * @return Nombre de souscriptions désactivées
     */
    int deactivateAllSubscriptionsForProcess(Long processInstanceId);
} 