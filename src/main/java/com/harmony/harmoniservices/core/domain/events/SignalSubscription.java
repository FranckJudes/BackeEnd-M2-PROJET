package com.harmony.harmoniservices.core.domain.events;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Représente un abonnement à un signal BPMN
 */
@Getter
public class SignalSubscription {
    
    private final String id;
    private final Long processInstanceId;
    private final String eventId;
    private final String signalName;
    private final Instant createdAt;
    
    @Setter(AccessLevel.PACKAGE)
    private boolean active;
    
    /**
     * Crée une nouvelle souscription à un signal
     * 
     * @param processInstanceId ID de l'instance de processus abonnée
     * @param eventId ID de l'événement BPMN concerné
     * @param signalName Nom du signal auquel s'abonner
     */
    public SignalSubscription(Long processInstanceId, String eventId, String signalName) {
        this.id = UUID.randomUUID().toString();
        this.processInstanceId = processInstanceId;
        this.eventId = eventId;
        this.signalName = signalName;
        this.createdAt = Instant.now();
        this.active = true;
    }
    
    /**
     * Désactive cette souscription
     */
    public void deactivate() {
        this.active = false;
    }
    
    /**
     * Vérifie si cette souscription correspond au signal donné
     * 
     * @param signalName Nom du signal à vérifier
     * @return true si cette souscription correspond au signal donné
     */
    public boolean matches(String signalName) {
        return active && Objects.equals(this.signalName, signalName);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignalSubscription that = (SignalSubscription) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("SignalSubscription[id=%s, processInstance=%d, event=%s, signal=%s, active=%s]",
                id, processInstanceId, eventId, signalName, active);
    }
} 