package com.harmony.harmoniservices.core.domain.events;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;

/**
 * Événement émis lorsqu'un signal BPMN est publié
 */
@Getter
public class SignalEvent {
    
    private final String id;
    private final String signalName;
    private final Map<String, Object> variables;
    private final Instant timestamp;
    private final String sourceProcessInstanceId;
    
    /**
     * Constructeur complet pour un événement de signal
     * 
     * @param signalName Nom du signal
     * @param variables Variables associées au signal
     * @param sourceProcessInstanceId ID de l'instance de processus source (peut être null)
     */
    public SignalEvent(String signalName, Map<String, Object> variables, String sourceProcessInstanceId) {
        this.id = UUID.randomUUID().toString();
        this.signalName = signalName;
        this.variables = variables != null ? variables : new HashMap<>();
        this.timestamp = Instant.now();
        this.sourceProcessInstanceId = sourceProcessInstanceId;
    }
    
    /**
     * Constructeur simplifié pour un événement de signal sans processus source identifié
     * 
     * @param signalName Nom du signal
     * @param variables Variables associées au signal
     */
    public SignalEvent(String signalName, Map<String, Object> variables) {
        this(signalName, variables, null);
    }
    
    /**
     * Vérifie si cet événement provient d'une instance de processus spécifique
     * 
     * @return true si l'événement a une source identifiée
     */
    public boolean hasSource() {
        return sourceProcessInstanceId != null && !sourceProcessInstanceId.isEmpty();
    }
    
    /**
     * Crée une copie de cet événement avec des variables supplémentaires
     * 
     * @param additionalVariables Variables à ajouter
     * @return Un nouvel événement avec les variables combinées
     */
    public SignalEvent withAdditionalVariables(Map<String, Object> additionalVariables) {
        if (additionalVariables == null || additionalVariables.isEmpty()) {
            return this;
        }
        
        Map<String, Object> combinedVariables = new HashMap<>(this.variables);
        combinedVariables.putAll(additionalVariables);
        
        return new SignalEvent(this.signalName, combinedVariables, this.sourceProcessInstanceId);
    }
    
    @Override
    public String toString() {
        return String.format("SignalEvent[id=%s, name=%s, source=%s, timestamp=%s, variables=%d]",
                id, signalName, sourceProcessInstanceId != null ? sourceProcessInstanceId : "none",
                timestamp, variables.size());
    }
} 