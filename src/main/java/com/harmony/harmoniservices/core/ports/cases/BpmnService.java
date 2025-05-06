package com.harmony.harmoniservices.core.ports.cases;

import java.io.InputStream;
import java.util.List;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import com.harmony.harmoniservices.core.domain.entities.BpmnData;
import com.harmony.harmoniservices.core.domain.entities.DataObject;
import com.harmony.harmoniservices.core.domain.entities.DataStore;
import com.harmony.harmoniservices.core.domain.entities.Event;
import com.harmony.harmoniservices.core.domain.entities.Gateway;
import com.harmony.harmoniservices.core.domain.entities.Lane;
import com.harmony.harmoniservices.core.domain.entities.LaneSet;
import com.harmony.harmoniservices.core.domain.entities.MessageFlow;
import com.harmony.harmoniservices.core.domain.entities.Pool;
import com.harmony.harmoniservices.core.domain.entities.SequenceFlow;
import com.harmony.harmoniservices.core.domain.entities.SubProcess;
import com.harmony.harmoniservices.core.domain.entities.Task;
import com.harmony.harmoniservices.core.domain.entities.TextAnnotation;
import com.harmony.harmoniservices.core.domain.enums.TriggerType;
import com.harmony.harmoniservices.core.domain.enums.TypeEvent;
import com.harmony.harmoniservices.core.domain.enums.TypeGateway;
import com.harmony.harmoniservices.core.domain.enums.TypeTask;

public interface BpmnService {

    BpmnData parseBpmnFile(InputStream filePath);
    List<Task> extractTasks(BpmnModelInstance bpmnModelInstance);
    List<Event> extractEvents(BpmnModelInstance bpmnModelInstance);
    List<Gateway> extractGateways(BpmnModelInstance bpmnModelInstance);
    List<SequenceFlow> extractSequenceFlows(BpmnModelInstance bpmnModelInstance);
    List<DataObject> extractDataObjects(BpmnModelInstance bpmnModelInstance);
    List<DataStore> extractDataStores(BpmnModelInstance bpmnModelInstance);
    List<TextAnnotation> extractTextAnnotations(BpmnModelInstance bpmnModelInstance);
    List<Lane> extractLanes(BpmnModelInstance bpmnModelInstance);
    List<LaneSet> extractLaneSets(BpmnModelInstance bpmnModelInstance);
    List<SubProcess> setSubProcesses(BpmnModelInstance bpmnModelInstance);
    List<Pool> extractPools(BpmnModelInstance bpmnModelInstance);
    List<MessageFlow> extractMessageFlows(BpmnModelInstance bpmnModelInstance);
    TriggerType determineTriggerType(org.camunda.bpm.model.bpmn.instance.Event event);
    String determineEventDefinition(org.camunda.bpm.model.bpmn.instance.Event event);
    TypeTask mapBpmnTaskTypeToEnum(String bpmnTaskType);
    TypeEvent mapBpmnEventTypeToEnum(String bpmnEventType);
    TypeGateway mapBpmnGatewayTypeToEnum(String bpmnGatewayType);
}
