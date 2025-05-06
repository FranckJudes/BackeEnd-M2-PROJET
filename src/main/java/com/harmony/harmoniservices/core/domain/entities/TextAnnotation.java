package com.harmony.harmoniservices.core.domain.entities;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TextAnnotation {
    private String id;
    private String text;
    private BpmnProcess process;
}