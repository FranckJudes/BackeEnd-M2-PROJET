package com.harmony.harmoniservices.core.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.harmony.harmoniservices.core.domain.enums.TypeGateway;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gateway {
    private String id;
    private String name;
    private TypeGateway typeGateway;
    private String documentation;
    private BpmnProcess process;
    
    @JsonBackReference
    private SubProcess subProcess;
}