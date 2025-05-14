package com.harmony.harmoniservices.core.domain.events;

import java.util.Map;

import lombok.Getter;

/**
 * Événement émis lorsqu'un timer BPMN est déclenché
 */
@Getter
public class TimerEvent {
    
    private final Long processInstanceId;
    private final String eventId;
    private final Map<String, Object> variables;
    
    public TimerEvent(Long processInstanceId, String eventId, Map<String, Object> variables) {
        this.processInstanceId = processInstanceId;
        this.eventId = eventId;
        this.variables = variables;
    }
} 