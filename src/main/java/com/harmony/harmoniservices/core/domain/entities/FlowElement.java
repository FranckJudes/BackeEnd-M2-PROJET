package com.harmony.harmoniservices.core.domain.entities;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlowElement {
    private String id;
    private SubProcess subProcess;
    private String type;
    private String name;
    private String documentation;
}