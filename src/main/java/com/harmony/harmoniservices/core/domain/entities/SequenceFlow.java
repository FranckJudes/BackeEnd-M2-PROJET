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
    
    /**
     * Retourne l'ID de l'élément source
     * @return ID de la source
     */
    public String getSourceRef() {
        return source != null ? source.getId() : null;
    }
    
    /**
     * Retourne l'ID de l'élément cible
     * @return ID de la cible
     */
    public String getTargetRef() {
        return target != null ? target.getId() : null;
    }
}