package com.harmony.harmoniservices.core.domain.entities;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlowNodeRef {
    private String id;
    private Lane lane;
    private String nodeRef;
}