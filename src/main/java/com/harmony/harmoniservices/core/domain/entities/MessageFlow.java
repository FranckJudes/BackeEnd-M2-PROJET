package com.harmony.harmoniservices.core.domain.entities;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageFlow {
    private String id;
    private String name;
    private String sourceRef;
    private String targetRef;
    private BpmnProcess process;
}
