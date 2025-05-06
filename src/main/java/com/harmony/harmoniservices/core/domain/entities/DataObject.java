package com.harmony.harmoniservices.core.domain.entities;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataObject {
    private String id;
    private String name;
    private BpmnProcess process;
    private String documentation;
}