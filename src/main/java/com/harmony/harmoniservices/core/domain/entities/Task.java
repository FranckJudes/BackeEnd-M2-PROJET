package com.harmony.harmoniservices.core.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.harmony.harmoniservices.core.domain.enums.TypeTask;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    private String id;
    private String name;
    private TypeTask typeTask;
    private String description;
    private BpmnProcess process;
    
    @JsonBackReference
    private SubProcess subProcess;
}