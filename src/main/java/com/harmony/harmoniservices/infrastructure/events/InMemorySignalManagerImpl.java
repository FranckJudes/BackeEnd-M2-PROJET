package com.harmony.harmoniservices.infrastructure.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.harmony.harmoniservices.core.domain.events.SignalEvent;
import com.harmony.harmoniservices.core.domain.events.SignalSubscription;
import com.harmony.harmoniservices.core.domain.services.SignalManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implémentation en mémoire du gestionnaire de signaux
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class InMemorySignalManagerImpl implements SignalManager {

    private final ApplicationEventPublisher eventPublisher;
    
    // Collections thread-safe pour stocker les souscriptions
    private final Map<String, SignalSubscription> subscriptionsById = new ConcurrentHashMap<>();
    private final Map<String, List<SignalSubscription>> subscriptionsBySignal = new ConcurrentHashMap<>();
    private final Map<Long, List<SignalSubscription>> subscriptionsByProcess = new ConcurrentHashMap<>();
    
    @Override
    public SignalEvent publishSignal(String signalName, Map<String, Object> variables, String sourceProcessInstanceId) {
        log.info("Publication du signal: {} (source: {})", signalName, 
                sourceProcessInstanceId != null ? sourceProcessInstanceId : "externe");
        
        // Créer l'événement de signal
        SignalEvent event = new SignalEvent(signalName, variables, sourceProcessInstanceId);
        
        // Publier l'événement dans le contexte Spring
        eventPublisher.publishEvent(event);
        
        return event;
    }
    
    @Override
    public SignalSubscription createSubscription(Long processInstanceId, String eventId, String signalName) {
        if (processInstanceId == null || eventId == null || signalName == null) {
            throw new IllegalArgumentException("processInstanceId, eventId et signalName ne peuvent pas être null");
        }
        
        log.info("Création d'une souscription au signal {} pour le processus {} (événement {})",
                signalName, processInstanceId, eventId);
        
        // Créer la nouvelle souscription
        SignalSubscription subscription = new SignalSubscription(processInstanceId, eventId, signalName);
        
        // Stocker la souscription dans les différentes maps
        subscriptionsById.put(subscription.getId(), subscription);
        
        // Ajouter à la map par signal
        subscriptionsBySignal.computeIfAbsent(signalName, k -> new CopyOnWriteArrayList<>())
                             .add(subscription);
        
        // Ajouter à la map par processus
        subscriptionsByProcess.computeIfAbsent(processInstanceId, k -> new CopyOnWriteArrayList<>())
                              .add(subscription);
        
        return subscription;
    }
    
    @Override
    public boolean cancelSubscription(String subscriptionId) {
        if (subscriptionId == null) {
            return false;
        }
        
        SignalSubscription subscription = subscriptionsById.get(subscriptionId);
        if (subscription == null) {
            log.warn("Tentative d'annulation d'une souscription inexistante: {}", subscriptionId);
            return false;
        }
        
        log.info("Annulation de la souscription {} au signal {} pour le processus {}",
                subscriptionId, subscription.getSignalName(), subscription.getProcessInstanceId());
        
        // Désactiver la souscription plutôt que de la supprimer
        // (la liste sera nettoyée lors des recherches)
        subscription.deactivate();
        return true;
    }
    
    @Override
    public List<SignalSubscription> findActiveSubscriptions(String signalName) {
        if (signalName == null) {
            return Collections.emptyList();
        }
        
        // Récupérer la liste et filtrer les souscriptions actives
        List<SignalSubscription> subscriptions = subscriptionsBySignal.getOrDefault(signalName, Collections.emptyList())
                                                                     .stream()
                                                                     .filter(SignalSubscription::isActive)
                                                                     .collect(Collectors.toList());
        
        log.debug("Recherche des souscriptions pour le signal {}: {} trouvées", signalName, subscriptions.size());
        return subscriptions;
    }
    
    @Override
    public SignalSubscription getSubscription(String subscriptionId) {
        if (subscriptionId == null) {
            return null;
        }
        
        SignalSubscription subscription = subscriptionsById.get(subscriptionId);
        
        // Ne pas retourner les souscriptions inactives
        if (subscription != null && !subscription.isActive()) {
            return null;
        }
        
        return subscription;
    }
    
    @Override
    public List<SignalSubscription> getActiveSubscriptionsForProcess(Long processInstanceId) {
        if (processInstanceId == null) {
            return Collections.emptyList();
        }
        
        // Récupérer la liste et filtrer les souscriptions actives
        List<SignalSubscription> subscriptions = subscriptionsByProcess.getOrDefault(processInstanceId, Collections.emptyList())
                                                                      .stream()
                                                                      .filter(SignalSubscription::isActive)
                                                                      .collect(Collectors.toList());
        
        log.debug("Recherche des souscriptions pour le processus {}: {} trouvées", processInstanceId, subscriptions.size());
        return subscriptions;
    }
    
    @Override
    public int deactivateAllSubscriptionsForProcess(Long processInstanceId) {
        if (processInstanceId == null) {
            return 0;
        }
        
        List<SignalSubscription> subscriptions = subscriptionsByProcess.getOrDefault(processInstanceId, Collections.emptyList());
        int count = 0;
        
        for (SignalSubscription subscription : subscriptions) {
            if (subscription.isActive()) {
                subscription.deactivate();
                count++;
            }
        }
        
        log.info("{} souscriptions désactivées pour le processus {}", count, processInstanceId);
        return count;
    }
    
    /**
     * Nettoie périodiquement les souscriptions inactives
     * Cette méthode pourrait être appelée par un job planifié
     */
    public void cleanupInactiveSubscriptions() {
        log.info("Nettoyage des souscriptions inactives");
        int count = 0;
        
        // Nettoyer subscriptionsBySignal
        for (Map.Entry<String, List<SignalSubscription>> entry : subscriptionsBySignal.entrySet()) {
            List<SignalSubscription> activeSubscriptions = new ArrayList<>();
            for (SignalSubscription subscription : entry.getValue()) {
                if (subscription.isActive()) {
                    activeSubscriptions.add(subscription);
                } else {
                    count++;
                }
            }
            
            if (activeSubscriptions.size() < entry.getValue().size()) {
                if (activeSubscriptions.isEmpty()) {
                    subscriptionsBySignal.remove(entry.getKey());
                } else {
                    entry.setValue(new CopyOnWriteArrayList<>(activeSubscriptions));
                }
            }
        }
        
        // Nettoyer subscriptionsByProcess
        for (Map.Entry<Long, List<SignalSubscription>> entry : subscriptionsByProcess.entrySet()) {
            List<SignalSubscription> activeSubscriptions = new ArrayList<>();
            for (SignalSubscription subscription : entry.getValue()) {
                if (subscription.isActive()) {
                    activeSubscriptions.add(subscription);
                }
            }
            
            if (activeSubscriptions.size() < entry.getValue().size()) {
                if (activeSubscriptions.isEmpty()) {
                    subscriptionsByProcess.remove(entry.getKey());
                } else {
                    entry.setValue(new CopyOnWriteArrayList<>(activeSubscriptions));
                }
            }
        }
        
        // Nettoyer subscriptionsById
        subscriptionsById.entrySet().removeIf(entry -> !entry.getValue().isActive());
        
        log.info("{} souscriptions inactives nettoyées", count);
    }
} 