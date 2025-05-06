package com.harmony.harmoniservices.core.domain.entities;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataAssociation {
    private String id;
    private String sourceRef;
    private String targetRef;
    private BpmnProcess process;
}