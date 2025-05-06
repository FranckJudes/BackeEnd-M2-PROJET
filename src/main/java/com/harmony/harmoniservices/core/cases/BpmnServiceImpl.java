package com.harmony.harmoniservices.core.cases;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.instance.*;
import org.springframework.stereotype.Service;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import com.harmony.harmoniservices.core.domain.entities.BpmnData;
import com.harmony.harmoniservices.core.domain.enums.TriggerType;
import com.harmony.harmoniservices.core.domain.enums.TypeEvent;
import com.harmony.harmoniservices.core.domain.enums.TypeGateway;
import com.harmony.harmoniservices.core.domain.enums.TypeTask;
import com.harmony.harmoniservices.core.ports.cases.BpmnService;

@Service
public class BpmnServiceImpl implements BpmnService {

    @Override
    public BpmnData parseBpmnFile(InputStream bpmnInputStream) {
        try {
            BpmnModelInstance modelInstance = Bpmn.readModelFromStream(bpmnInputStream);
            BpmnData bpmnData = new BpmnData();

            bpmnData.setTasks(extractTasks(modelInstance));
            bpmnData.setEvents(extractEvents(modelInstance));
            bpmnData.setGateways(extractGateways(modelInstance));
            bpmnData.setSequenceFlows(extractSequenceFlows(modelInstance));
            bpmnData.setDataObjects(extractDataObjects(modelInstance));
            bpmnData.setDataStores(extractDataStores(modelInstance));
            bpmnData.setTextAnnotations(extractTextAnnotations(modelInstance));
            bpmnData.setLanes(extractLanes(modelInstance));
            bpmnData.setLaneSets(extractLaneSets(modelInstance));
            bpmnData.setSubProcesses(setSubProcesses(modelInstance));
            bpmnData.setPools(extractPools(modelInstance));
            bpmnData.setMessageFlows(extractMessageFlows(modelInstance));

            return bpmnData;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing BPMN file", e);
        }
    }

    @Override
    public List<com.harmony.harmoniservices.core.domain.entities.Task> extractTasks(BpmnModelInstance modelInstance) {
        return modelInstance.getModelElementsByType(Task.class)
                .stream()
                .filter(task -> !(task.getParentElement() instanceof org.camunda.bpm.model.bpmn.instance.SubProcess))
                .map(task -> com.harmony.harmoniservices.core.domain.entities.Task.builder()
                        .id(task.getId())
                        .name(task.getName())
                        .typeTask(mapBpmnTaskTypeToEnum(task.getElementType().getTypeName()))
                        .description(!task.getDocumentations().isEmpty()
                                ? task.getDocumentations().iterator().next().getTextContent()
                                : null)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<com.harmony.harmoniservices.core.domain.entities.Event> extractEvents(BpmnModelInstance modelInstance) {
        return modelInstance.getModelElementsByType(Event.class)
                .stream()
                .map(event -> com.harmony.harmoniservices.core.domain.entities.Event.builder()
                        .id(event.getId())
                        .name(event.getName())
                        .typeEvent(mapBpmnEventTypeToEnum(event.getElementType().getTypeName()))
                        .triggerType(determineTriggerType(event))
                        .eventDefinition(determineEventDefinition(event))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<com.harmony.harmoniservices.core.domain.entities.Gateway> extractGateways(BpmnModelInstance modelInstance) {
        return modelInstance.getModelElementsByType(Gateway.class)
                .stream()
                .map(gateway -> com.harmony.harmoniservices.core.domain.entities.Gateway.builder()
                        .id(gateway.getId())
                        .name(gateway.getName())
                        .typeGateway(mapBpmnGatewayTypeToEnum(gateway.getElementType().getTypeName()))
                        .documentation(gateway.getDocumentations().isEmpty()
                                ? null : gateway.getDocumentations().iterator().next().getTextContent())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<com.harmony.harmoniservices.core.domain.entities.SequenceFlow> extractSequenceFlows(BpmnModelInstance modelInstance) {
        return modelInstance.getModelElementsByType(SequenceFlow.class)
                .stream()
                .map(flow -> {
                    com.harmony.harmoniservices.core.domain.entities.Task sourceTask =
                            com.harmony.harmoniservices.core.domain.entities.Task.builder()
                                    .id(flow.getSource().getId())
                                    .build();
                    com.harmony.harmoniservices.core.domain.entities.Task targetTask =
                            com.harmony.harmoniservices.core.domain.entities.Task.builder()
                                    .id(flow.getTarget().getId())
                                    .build();
                    return com.harmony.harmoniservices.core.domain.entities.SequenceFlow.builder()
                            .id(flow.getId())
                            .name(flow.getName())
                            .source(sourceTask)
                            .target(targetTask)
                            .conditionExpression(flow.getConditionExpression() != null
                                    ? flow.getConditionExpression().getTextContent()
                                    : null)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<com.harmony.harmoniservices.core.domain.entities.DataObject> extractDataObjects(BpmnModelInstance modelInstance) {
        return modelInstance.getModelElementsByType(DataObject.class)
                .stream()
                .map(obj -> new com.harmony.harmoniservices.core.domain.entities.DataObject(
                        obj.getId(),
                        obj.getName(),
                        null,
                        !obj.getDocumentations().isEmpty() ?
                                obj.getDocumentations().iterator().next().getTextContent() : null
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<com.harmony.harmoniservices.core.domain.entities.DataStore> extractDataStores(BpmnModelInstance modelInstance) {
        return modelInstance.getModelElementsByType(DataStore.class)
                .stream()
                .map(store -> new com.harmony.harmoniservices.core.domain.entities.DataStore(
                        store.getId(),
                        store.getName(),
                        null,
                        !store.getDocumentations().isEmpty() ?
                                store.getDocumentations().iterator().next().getTextContent() : null
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<com.harmony.harmoniservices.core.domain.entities.LaneSet> extractLaneSets(BpmnModelInstance modelInstance) {
        return modelInstance.getModelElementsByType(org.camunda.bpm.model.bpmn.instance.LaneSet.class)
                .stream()
                .map(camundaLaneSet -> com.harmony.harmoniservices.core.domain.entities.LaneSet.builder()
                        .id(camundaLaneSet.getId())
                        .name(camundaLaneSet.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<com.harmony.harmoniservices.core.domain.entities.Lane> extractLanes(BpmnModelInstance modelInstance) {
        return modelInstance.getModelElementsByType(Lane.class)
                .stream()
                .map(lane -> new com.harmony.harmoniservices.core.domain.entities.Lane(
                        lane.getId(),
                        lane.getName(),
                        null
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<com.harmony.harmoniservices.core.domain.entities.SubProcess> setSubProcesses(BpmnModelInstance modelInstance) {
        return modelInstance.getModelElementsByType(org.camunda.bpm.model.bpmn.instance.SubProcess.class)
                .stream()
                .filter(subProcess -> !(subProcess.getParentElement() instanceof org.camunda.bpm.model.bpmn.instance.SubProcess))
                .map(this::extractSubProcessWithNested)
                .collect(Collectors.toList());
    }

    @Override
    public List<com.harmony.harmoniservices.core.domain.entities.Pool> extractPools(BpmnModelInstance modelInstance) {
        return modelInstance.getModelElementsByType(Participant.class)
                .stream()
                .map(p -> com.harmony.harmoniservices.core.domain.entities.Pool.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<com.harmony.harmoniservices.core.domain.entities.MessageFlow> extractMessageFlows(BpmnModelInstance modelInstance) {
        return modelInstance.getModelElementsByType(MessageFlow.class)
                .stream()
                .map(mf -> new com.harmony.harmoniservices.core.domain.entities.MessageFlow(
                        mf.getId(),
                        mf.getName(),
                        mf.getSource().getId(),
                        mf.getTarget().getId(),
                        null
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<com.harmony.harmoniservices.core.domain.entities.TextAnnotation> extractTextAnnotations(BpmnModelInstance modelInstance) {
        return modelInstance.getModelElementsByType(org.camunda.bpm.model.bpmn.instance.TextAnnotation.class)
                .stream()
                .map(annotation -> new com.harmony.harmoniservices.core.domain.entities.TextAnnotation(
                        annotation.getId(),
                        annotation.getTextContent(),
                        null
                ))
                .collect(Collectors.toList());
    }

    @Override
    public TriggerType determineTriggerType(org.camunda.bpm.model.bpmn.instance.Event event) {
        if (event.getDocumentations().isEmpty())
            return TriggerType.NONE;

        String eventDefType = event.getDocumentations()
                .iterator()
                .next()
                .getElementType()
                .getTypeName()
                .toUpperCase();

        switch (eventDefType) {
            case "MESSAGEEVENTDEFINITION":
                return TriggerType.MESSAGE;
            case "SIGNALEVENTDEFINITION":
                return TriggerType.SIGNAL;
            case "TIMEREVENTDEFINITION":
                return TriggerType.TIMER;
            case "CONDITIONALEVENTDEFINITION":
                return TriggerType.CONDITIONAL;
            case "ERROREVENTDEFINITION":
                return TriggerType.ERROR;
            case "ESCALATIONEVENTDEFINITION":
                return TriggerType.ESCALATION;
            case "COMPENSATIONEVENTDEFINITION":
                return TriggerType.COMPENSATION;
            case "LINKEVENTDEFINITION":
                return TriggerType.LINK;
            case "TERMINATEEVENTDEFINITION":
                return TriggerType.TERMINATE;
            default:
                return TriggerType.NONE;
        }
    }

    @Override
    public String determineEventDefinition(org.camunda.bpm.model.bpmn.instance.Event event) {
        if (event.getDocumentations().isEmpty())
            return null;

        var eventDefinition = event.getDocumentations().iterator().next();
        StringBuilder definition = new StringBuilder();

        definition.append(eventDefinition.getElementType().getTypeName());

        if (eventDefinition instanceof MessageEventDefinition) {
            MessageEventDefinition med = (MessageEventDefinition) eventDefinition;
            if (med.getMessage() != null)
                definition.append(": ").append(med.getMessage().getName());
        } else if (eventDefinition instanceof TimerEventDefinition) {
            TimerEventDefinition ted = (TimerEventDefinition) eventDefinition;
            if (ted.getTimeDuration() != null)
                definition.append(": Duration=").append(ted.getTimeDuration().getTextContent());
            else if (ted.getTimeDate() != null)
                definition.append(": Date=").append(ted.getTimeDate().getTextContent());
            else if (ted.getTimeCycle() != null)
                definition.append(": Cycle=").append(ted.getTimeCycle().getTextContent());
        } else if (eventDefinition instanceof ErrorEventDefinition) {
            ErrorEventDefinition eed = (ErrorEventDefinition) eventDefinition;
            if (eed.getError() != null)
                definition.append(": ").append(eed.getError().getErrorCode());
        } else if (eventDefinition instanceof SignalEventDefinition) {
            SignalEventDefinition sed = (SignalEventDefinition) eventDefinition;
            if (sed.getSignal() != null)
                definition.append(": ").append(sed.getSignal().getName());
        }

        return definition.toString();
    }

    @Override
    public TypeTask mapBpmnTaskTypeToEnum(String bpmnTaskType) {
        switch (bpmnTaskType.toLowerCase()) {
            case "usertask":
                return TypeTask.USER;
            case "servicetask":
                return TypeTask.SERVICE;
            case "scripttask":
                return TypeTask.SCRIPT;
            case "businessruletask":
                return TypeTask.BUSINESS_RULE;
            case "sendtask":
                return TypeTask.SEND;
            case "receivetask":
                return TypeTask.RECEIVE;
            case "manualtask":
                return TypeTask.MANUAL;
            default:
                return TypeTask.NONE;
        }
    }

    @Override
    public TypeEvent mapBpmnEventTypeToEnum(String bpmnEventType) {
        switch (bpmnEventType.toLowerCase()) {
            case "startevent":
                return TypeEvent.START;
            case "endevent":
                return TypeEvent.END;
            case "intermediatecatchevent":
                return TypeEvent.INTERMEDIATE_CATCH_EVENT;
            case "intermediatethrowevent":
                return TypeEvent.INTERMEDIATE_THROW_EVENT;
            case "boundaryevent":
                return TypeEvent.BOUNDARY_EVENT;
            case "messageintermediatecatchevent":
                return TypeEvent.MESSAGE_INTERMEDITE_CATCH_EVENT;
            case "messageintermediatethrowevent":
                return TypeEvent.MESSAGE_INTERMEDITE_THROW_EVENT;
            case "signalintermediatecatchevent":
                return TypeEvent.SIGNAL_INTERMEDITE_CATCH_EVENT;
            case "signalintermediatethrowevent":
                return TypeEvent.SIGNAL_INTERMEDITE_THROW_EVENT;
            case "timerintermediatecatchevent":
                return TypeEvent.TIMER_INTERMEDITE_CATCH_EVENT;
            case "timerintermediatethrowevent":
                return TypeEvent.TIMER_INTERMEDITE_THROW_EVENT;
            case "compensationintermediatecatchevent":
                return TypeEvent.COMPENSATION_INTERMEDITE_CATCH_EVENT;
            case "compensationintermediatethrowevent":
                return TypeEvent.COMPENSATION_INTERMEDITE_THROW_EVENT;
            case "linkintermediatecatchevent":
                return TypeEvent.LINK_INTERMEDITE_CATCH_EVENT;
            case "linkintermediatethrowevent":
                return TypeEvent.LINK_INTERMEDITE_THROW_EVENT;
            case "terminateintermediatecatchevent":
                return TypeEvent.TERMINATE_INTERMEDITE_CATCH_EVENT;
            case "terminateintermediatethrowevent":
                return TypeEvent.TERMINATE_INTERMEDITE_THROW_EVENT;
            case "intermediateevent":
                return TypeEvent.INTERMEDIATE;
            default:
                return TypeEvent.UNKNOWN;
        }
    }

    @Override
    public TypeGateway mapBpmnGatewayTypeToEnum(String bpmnGatewayType) {
        switch (bpmnGatewayType.toLowerCase()) {
            case "exclusivegateway":
                return TypeGateway.EXCLUSIVE;
            case "parallelgateway":
                return TypeGateway.PARALLEL;
            case "inclusivegateway":
                return TypeGateway.INCLUSIVE;
            case "complexgateway":
                return TypeGateway.COMPLEX;
            case "eventbasedgateway":
                return TypeGateway.EVENT_BASED;
            default:
                return TypeGateway.UNKNOWN;
        }
    }

    // Méthodes helpers internes

    private com.harmony.harmoniservices.core.domain.entities.SubProcess extractSubProcessWithNested(org.camunda.bpm.model.bpmn.instance.SubProcess bpmnSubProcess) {
        com.harmony.harmoniservices.core.domain.entities.SubProcess entitySubProcess =
                com.harmony.harmoniservices.core.domain.entities.SubProcess.builder()
                        .id(bpmnSubProcess.getId())
                        .name(bpmnSubProcess.getName())
                        .documentation(!bpmnSubProcess.getDocumentations().isEmpty()
                                ? bpmnSubProcess.getDocumentations().iterator().next().getTextContent()
                                : null)
                        .tasks(new ArrayList<>())
                        .events(new ArrayList<>())
                        .gateways(new ArrayList<>())
                        .sequenceFlows(new ArrayList<>())
                        .subProcesses(new ArrayList<>())
                        .build();

        List<com.harmony.harmoniservices.core.domain.entities.Task> tasks = extractTasksFromSubProcess(bpmnSubProcess, entitySubProcess);
        List<com.harmony.harmoniservices.core.domain.entities.Event> events = extractEventsFromSubProcess(bpmnSubProcess, entitySubProcess);
        List<com.harmony.harmoniservices.core.domain.entities.Gateway> gateways = extractGatewaysFromSubProcess(bpmnSubProcess, entitySubProcess);
        List<com.harmony.harmoniservices.core.domain.entities.SequenceFlow> sequenceFlows = extractSequenceFlowsFromSubProcess(bpmnSubProcess, entitySubProcess);

        entitySubProcess.getTasks().addAll(tasks);
        entitySubProcess.getEvents().addAll(events);
        entitySubProcess.getGateways().addAll(gateways);
        entitySubProcess.getSequenceFlows().addAll(sequenceFlows);

        List<com.harmony.harmoniservices.core.domain.entities.SubProcess> nestedSubProcesses =
                bpmnSubProcess.getChildElementsByType(org.camunda.bpm.model.bpmn.instance.SubProcess.class)
                        .stream()
                        .map(this::extractSubProcessWithNested)
                        .collect(Collectors.toList());

        entitySubProcess.getSubProcesses().addAll(nestedSubProcesses);

        return entitySubProcess;
    }

    private List<com.harmony.harmoniservices.core.domain.entities.Task> extractTasksFromSubProcess(
            org.camunda.bpm.model.bpmn.instance.SubProcess bpmnSubProcess,
            com.harmony.harmoniservices.core.domain.entities.SubProcess entitySubProcess) {
        return bpmnSubProcess.getChildElementsByType(org.camunda.bpm.model.bpmn.instance.Task.class)
                .stream()
                .map(task -> com.harmony.harmoniservices.core.domain.entities.Task.builder()
                        .id(task.getId())
                        .name(task.getName())
                        .typeTask(mapBpmnTaskTypeToEnum(task.getElementType().getTypeName()))
                        .description(!task.getDocumentations().isEmpty()
                                ? task.getDocumentations().iterator().next().getTextContent()
                                : null)
                        .subProcess(entitySubProcess)
                        .build())
                .collect(Collectors.toList());
    }

    private List<com.harmony.harmoniservices.core.domain.entities.Event> extractEventsFromSubProcess(
            org.camunda.bpm.model.bpmn.instance.SubProcess bpmnSubProcess,
            com.harmony.harmoniservices.core.domain.entities.SubProcess entitySubProcess) {
        return bpmnSubProcess.getChildElementsByType(org.camunda.bpm.model.bpmn.instance.Event.class)
                .stream()
                .map(event -> com.harmony.harmoniservices.core.domain.entities.Event.builder()
                        .id(event.getId())
                        .name(event.getName())
                        .typeEvent(mapBpmnEventTypeToEnum(event.getElementType().getTypeName()))
                        .triggerType(determineTriggerType(event))
                        .eventDefinition(determineEventDefinition(event))
                        .subProcess(entitySubProcess)
                        .build())
                .collect(Collectors.toList());
    }

    private List<com.harmony.harmoniservices.core.domain.entities.Gateway> extractGatewaysFromSubProcess(
            org.camunda.bpm.model.bpmn.instance.SubProcess bpmnSubProcess,
            com.harmony.harmoniservices.core.domain.entities.SubProcess entitySubProcess) {
        return bpmnSubProcess.getChildElementsByType(org.camunda.bpm.model.bpmn.instance.Gateway.class)
                .stream()
                .map(gateway -> com.harmony.harmoniservices.core.domain.entities.Gateway.builder()
                        .id(gateway.getId())
                        .name(gateway.getName())
                        .typeGateway(mapBpmnGatewayTypeToEnum(gateway.getElementType().getTypeName()))
                        .documentation(gateway.getDocumentations().isEmpty()
                                ? null
                                : gateway.getDocumentations().iterator().next().getTextContent())
                        .subProcess(entitySubProcess)
                        .build())
                .collect(Collectors.toList());
    }

    private List<com.harmony.harmoniservices.core.domain.entities.SequenceFlow> extractSequenceFlowsFromSubProcess(
            org.camunda.bpm.model.bpmn.instance.SubProcess bpmnSubProcess,
            com.harmony.harmoniservices.core.domain.entities.SubProcess entitySubProcess) {
        return bpmnSubProcess.getChildElementsByType(org.camunda.bpm.model.bpmn.instance.SequenceFlow.class)
                .stream()
                .map(flow -> com.harmony.harmoniservices.core.domain.entities.SequenceFlow.builder()
                        .id(flow.getId())
                        .name(flow.getName())
                        .source(com.harmony.harmoniservices.core.domain.entities.Task.builder()
                                .id(flow.getSource().getId())
                                .build())
                        .target(com.harmony.harmoniservices.core.domain.entities.Task.builder()
                                .id(flow.getTarget().getId())
                                .build())
                        .conditionExpression(flow.getConditionExpression() != null
                                ? flow.getConditionExpression().getTextContent()
                                : null)
                        .subProcess(entitySubProcess)
                        .build())
                .collect(Collectors.toList());
    }
}