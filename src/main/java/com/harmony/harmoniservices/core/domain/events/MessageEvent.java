package com.harmony.harmoniservices.core.domain.events;

import java.util.Map;

import lombok.Getter;

/**
 * Événement émis lorsqu'un message BPMN est publié
 */
@Getter
public class MessageEvent {
    
    private final String messageName;
    private final String correlationKey;
    private final Map<String, Object> variables;
    
    public MessageEvent(String messageName, String correlationKey, Map<String, Object> variables) {
        this.messageName = messageName;
        this.correlationKey = correlationKey;
        this.variables = variables;
    }
} 