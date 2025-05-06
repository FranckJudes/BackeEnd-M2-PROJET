package com.harmony.harmoniservices.core.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.harmony.harmoniservices.core.domain.enums.TriggerType;
import com.harmony.harmoniservices.core.domain.enums.TypeEvent;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    private String id;
    private String name;
    private TypeEvent typeEvent;
    private TriggerType triggerType;
    private String eventDefinition;
    private BpmnProcess process;
    
    @JsonBackReference
    private SubProcess subProcess;
}