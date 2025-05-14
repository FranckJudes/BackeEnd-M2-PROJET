package com.harmony.harmoniservices.infrastructure.events;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.harmony.harmoniservices.core.domain.events.SignalEvent;
import com.harmony.harmoniservices.core.domain.events.SignalSubscription;
import com.harmony.harmoniservices.core.domain.services.SignalManager;
import com.harmony.harmoniservices.core.ports.cases.ProcessAutomationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Écouteur d'événements pour traiter les événements de signal BPMN
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SignalEventListener {

    private final SignalManager signalManager;
    private final ProcessAutomationService processAutomationService;

    /**
     * Traite les événements de signal
     * @param event Événement de signal reçu
     */
    @EventListener
    @Async
    @Transactional
    public void handleSignalEvent(SignalEvent event) {
        log.info("Réception d'un événement de signal: {} (id: {})", event.getSignalName(), event.getId());
        
        // Trouver toutes les souscriptions actives pour ce signal
        List<SignalSubscription> subscriptions = signalManager.findActiveSubscriptions(event.getSignalName());
        
        if (subscriptions.isEmpty()) {
            log.info("Aucune souscription active trouvée pour le signal: {}", event.getSignalName());
            return;
        }
        
        log.info("Traitement de {} souscriptions pour le signal: {}", subscriptions.size(), event.getSignalName());
        
        // Traiter chaque souscription
        for (SignalSubscription subscription : subscriptions) {
            // Si l'événement provient de la même instance de processus, l'ignorer
            // pour éviter les boucles infinies
            if (event.hasSource() && 
                event.getSourceProcessInstanceId().equals(subscription.getProcessInstanceId().toString())) {
                log.debug("Ignoré: la souscription {} appartient à l'instance source", subscription.getId());
                continue;
            }
            
            try {
                // Déclencher l'événement dans l'instance de processus
                log.debug("Déclenchement de l'événement {} dans l'instance {}", 
                        subscription.getEventId(), subscription.getProcessInstanceId());
                
                boolean success = processAutomationService.triggerEvent(
                        subscription.getProcessInstanceId(), 
                        subscription.getEventId(), 
                        event.getVariables());
                
                if (success) {
                    log.info("Événement {} déclenché avec succès dans l'instance {}", 
                            subscription.getEventId(), subscription.getProcessInstanceId());
                    
                    // Désactiver la souscription si l'événement a été déclenché avec succès
                    // car les événements de signal dans BPMN sont généralement one-shot
                    signalManager.cancelSubscription(subscription.getId());
                } else {
                    log.warn("Échec du déclenchement de l'événement {} dans l'instance {}", 
                            subscription.getEventId(), subscription.getProcessInstanceId());
                }
            } catch (Exception e) {
                log.error("Erreur lors du traitement de la souscription {} pour le signal {}: {}", 
                        subscription.getId(), event.getSignalName(), e.getMessage(), e);
            }
        }
    }
} 