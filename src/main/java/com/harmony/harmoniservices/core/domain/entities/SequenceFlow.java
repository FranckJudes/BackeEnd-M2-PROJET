package com.harmony.harmoniservices.core.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SequenceFlow {
    private String id;
    private String name;
    private Task source;
    private Task target;
    private String conditionExpression;
    private BpmnProcess process;

    @JsonBackReference
    private SubProcess subProcess;
}