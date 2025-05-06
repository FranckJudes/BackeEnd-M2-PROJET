package com.harmony.harmoniservices.core.domain.entities;

import lombok.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubProcess {
    private String id;
    private String name;
    private BpmnProcess process;
    private String documentation;
    
    @Builder.Default
    @JsonManagedReference
    private List<Task> tasks = new ArrayList<>();
    
    @Builder.Default
    @JsonManagedReference
    private List<Event> events = new ArrayList<>();
    
    @Builder.Default
    private List<Gateway> gateways = new ArrayList<>();
    
    @Builder.Default
    private List<SequenceFlow> sequenceFlows = new ArrayList<>();
    
    
    @Builder.Default
    @JsonManagedReference
    private List<SubProcess> subProcesses = new ArrayList<>();
}