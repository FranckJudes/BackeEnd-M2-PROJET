package com.harmony.harmoniservices.core.domain.entities;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtensionElement {
    private String id;
    private String elementId;
    private String name;
    private String value;
    private BpmnProcess process;
}