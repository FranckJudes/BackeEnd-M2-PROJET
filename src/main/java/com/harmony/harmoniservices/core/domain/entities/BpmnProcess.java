package com.harmony.harmoniservices.core.domain.entities;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BpmnProcess {
    private String id;
    private String name;
    private Boolean isExecutable;
    private String description;
    private String keywords;
    private String imagePaths;
    private String filePaths;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Relations avec d'autres éléments BPMN
    private List<Task> tasks;
    private List<Event> events;
    private List<Gateway> gateways;
    private List<SequenceFlow> sequenceFlows;
    private List<DataObject> dataObjects;
    private List<DataStore> dataStores;
    private List<TextAnnotation> textAnnotations;
    private List<Lane> lanes;
    private List<LaneSet> laneSets;
    private List<SubProcess> subProcesses;
    private List<Pool> pools;
    private List<MessageFlow> messageFlows;
}