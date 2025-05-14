package com.harmony.harmoniservices.core.cases;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.harmony.harmoniservices.core.domain.entities.BpmnProcess;
import com.harmony.harmoniservices.core.domain.entities.Event;
import com.harmony.harmoniservices.core.domain.entities.Gateway;
import com.harmony.harmoniservices.core.domain.entities.ProcessExecution;
import com.harmony.harmoniservices.core.domain.entities.ProcessInstance;
import com.harmony.harmoniservices.core.domain.entities.SequenceFlow;
import com.harmony.harmoniservices.core.domain.entities.Task;
import com.harmony.harmoniservices.core.domain.entities.TaskConfiguration;
import com.harmony.harmoniservices.core.domain.entities.User;
import com.harmony.harmoniservices.core.domain.enums.TriggerType;
import com.harmony.harmoniservices.core.domain.enums.TypeGateway;
import com.harmony.harmoniservices.core.domain.events.MessageEvent;
import com.harmony.harmoniservices.core.domain.events.SignalEvent;
import com.harmony.harmoniservices.core.domain.events.TimerEvent;
import com.harmony.harmoniservices.core.domain.services.EventHandler;
import com.harmony.harmoniservices.core.domain.services.GatewayEvaluator;
import com.harmony.harmoniservices.core.ports.cases.BpmnService;
import com.harmony.harmoniservices.core.ports.cases.ProcessAutomationService;
import com.harmony.harmoniservices.core.ports.repository.ProcessInstanceRepository;
import com.harmony.harmoniservices.core.ports.repository.ProcessExecutionRepository;
import com.harmony.harmoniservices.core.ports.repository.TaskConfigurationRepository;
import com.harmony.harmoniservices.core.ports.repository.BpmnProcessRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessAutomationServiceImpl implements ProcessAutomationService {

    private final BpmnService bpmnService;
    private final ProcessInstanceRepository processInstanceRepository;
    private final ProcessExecutionRepository processExecutionRepository;
    private final TaskConfigurationRepository taskConfigurationRepository;
    private final BpmnProcessRepository bpmnProcessRepository;
    private final GatewayEvaluator gatewayEvaluator;
    private final EventHandler eventHandler;

    @Override
    @Transactional
    public ProcessInstance startProcess(String processId, User initiator, Map<String, Object> variables, String businessKey) {
        log.info("Démarrage du processus {} par l'utilisateur {}", processId, initiator.getId());
        
        // Récupérer le processus BPMN
        BpmnProcess process = bpmnProcessRepository.findById(processId)
                .orElseThrow(() -> new IllegalArgumentException("Processus non trouvé: " + processId));
        
        // Créer une nouvelle instance de processus
        ProcessInstance instance = ProcessInstance.builder()
                .process(process)
                .status("ACTIVE")
                .startTime(LocalDateTime.now())
                .initiator(initiator)
                .businessKey(businessKey)
                .processVariables(variables != null ? variables : new HashMap<>())
                .executionHistory(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // Déterminer la première tâche ou événement
        String firstElementId = findStartElement(process);
        instance.setCurrentTaskId(firstElementId);
        
        // Persister l'instance
        ProcessInstance savedInstance = processInstanceRepository.save(instance);
        
        // Créer une entrée dans l'historique pour le démarrage du processus
        ProcessExecution execution = ProcessExecution.builder()
                .processInstanceId(savedInstance.getId())
                .taskId("start")
                .taskName("Démarrage du processus")
                .status("COMPLETED")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .executor(initiator)
                .result("Processus démarré")
                .createdAt(LocalDateTime.now())
                .build();
        
        processExecutionRepository.save(execution);
        
        // Configurer les événements de démarrage (timer, message, signal)
        setupInitialEvents(savedInstance);
        
        return savedInstance;
    }

    @Override
    @Transactional
    public ProcessInstance completeTask(Long processInstanceId, String taskId, User user, Map<String, Object> variables) {
        log.info("Achèvement de la tâche {} dans le processus {} par l'utilisateur {}", 
                taskId, processInstanceId, user.getId());
        
        // Récupérer l'instance de processus
        ProcessInstance instance = processInstanceRepository.findById(processInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Instance de processus non trouvée: " + processInstanceId));
        
        // Vérifier que la tâche est bien la tâche courante
        if (!taskId.equals(instance.getCurrentTaskId())) {
            throw new IllegalStateException("La tâche " + taskId + " n'est pas la tâche courante de l'instance de processus");
        }
        
        // Vérifier que l'utilisateur est autorisé à compléter cette tâche
        verifyUserAuthorization(instance, taskId, user);
        
        // Mettre à jour les variables du processus
        if (variables != null) {
            Map<String, Object> processVariables = instance.getProcessVariables();
            processVariables.putAll(variables);
            instance.setProcessVariables(processVariables);
        }
        
        // Créer une entrée dans l'historique
        LocalDateTime now = LocalDateTime.now();
        ProcessExecution execution = ProcessExecution.builder()
                .processInstanceId(instance.getId())
                .taskId(taskId)
                .taskName(getTaskName(instance.getProcess(), taskId))
                .status("COMPLETED")
                .startTime(now) // Idéalement, on récupérerait le startTime réel
                .endTime(now)
                .executor(user)
                .result("Tâche complétée")
                .createdAt(now)
                .build();
        
        processExecutionRepository.save(execution);
        
        // Déterminer la/les prochaine(s) tâche(s)
        List<String> nextElementIds = determineNextElements(instance.getProcess(), taskId, instance.getProcessVariables());
        
        // Si pas de prochaine tâche, terminer le processus
        if (nextElementIds.isEmpty()) {
            instance.setStatus("COMPLETED");
            instance.setEndTime(now);
            instance.setCurrentTaskId(null);
        } 
        // Si plusieurs tâches suivantes (après passerelle parallèle), gérer les chemins parallèles
        else if (nextElementIds.size() > 1) {
            // Dans une implémentation complète, nous gérerions ici les chemins parallèles avec un moteur
            // comme Camunda. Pour cette implémentation simplifiée, nous prenons juste le premier élément.
            instance.setCurrentTaskId(nextElementIds.get(0));
            log.warn("Plusieurs chemins détectés après une passerelle. Seul le premier est suivi dans cette implémentation.");
        }
        // Cas normal: une seule tâche suivante
        else {
            instance.setCurrentTaskId(nextElementIds.get(0));
            
            // Vérifier si le prochain élément est un événement et le configurer si nécessaire
            setupEventIfNeeded(instance, nextElementIds.get(0));
        }
        
        instance.setUpdatedAt(now);
        
        // Persister l'instance mise à jour
        return processInstanceRepository.save(instance);
    }

    @Override
    public List<ProcessInstance> getUserTasks(Long userId) {
        log.info("Récupération des tâches assignées à l'utilisateur {}", userId);
        
        // Récupérer toutes les instances de processus actives où l'utilisateur est assigné à la tâche courante
        return processInstanceRepository.findActiveInstancesWithUserTasks(userId);
    }

    @Override
    public List<ProcessInstance> getGroupTasks(Long groupId) {
        log.info("Récupération des tâches disponibles pour le groupe {}", groupId);
        
        // Récupérer toutes les instances de processus actives où le groupe est autorisé à effectuer la tâche courante
        return processInstanceRepository.findActiveInstancesWithGroupTasks(groupId);
    }

    @Override
    public Optional<ProcessInstance> getProcessInstance(Long processInstanceId) {
        return processInstanceRepository.findById(processInstanceId);
    }

    @Override
    public List<ProcessInstance> getActiveProcessInstances(String processId) {
        return processInstanceRepository.findActiveInstancesByProcessId(processId);
    }

    @Override
    @Transactional
    public TaskConfiguration configureTask(String processId, String taskId, TaskConfiguration configuration) {
        log.info("Configuration de la tâche {} dans le processus {}", taskId, processId);
        
        // Vérifier que le processus existe
        BpmnProcess process = bpmnProcessRepository.findById(processId)
                .orElseThrow(() -> new IllegalArgumentException("Processus non trouvé: " + processId));
        
        // Vérifier que la tâche existe dans le processus
        boolean taskExists = process.getTasks().stream()
                .anyMatch(task -> task.getId().equals(taskId));
        
        if (!taskExists) {
            throw new IllegalArgumentException("Tâche non trouvée dans le processus: " + taskId);
        }
        
        // Configurer la tâche
        configuration.setTaskId(taskId);
        configuration.setCreatedAt(LocalDateTime.now());
        configuration.setUpdatedAt(LocalDateTime.now());
        
        // Supprimer la configuration existante si elle existe
        taskConfigurationRepository.deleteByTaskId(taskId);
        
        // Persister la nouvelle configuration
        return taskConfigurationRepository.save(configuration);
    }

    @Override
    public Optional<TaskConfiguration> getTaskConfiguration(String processId, String taskId) {
        return taskConfigurationRepository.findByTaskId(taskId);
    }

    @Override
    public List<TaskConfiguration> getProcessTaskConfigurations(String processId) {
        // Récupérer toutes les tâches du processus
        BpmnProcess process = bpmnProcessRepository.findById(processId)
                .orElseThrow(() -> new IllegalArgumentException("Processus non trouvé: " + processId));
        
        List<String> taskIds = process.getTasks().stream()
                .map(Task::getId)
                .collect(Collectors.toList());
        
        // Récupérer les configurations pour ces tâches
        return taskConfigurationRepository.findByTaskIdIn(taskIds);
    }

    @Override
    @Transactional
    public ProcessInstance suspendProcess(Long processInstanceId) {
        ProcessInstance instance = processInstanceRepository.findById(processInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Instance de processus non trouvée: " + processInstanceId));
        
        instance.setStatus("SUSPENDED");
        instance.setUpdatedAt(LocalDateTime.now());
        
        return processInstanceRepository.save(instance);
    }

    @Override
    @Transactional
    public ProcessInstance resumeProcess(Long processInstanceId) {
        ProcessInstance instance = processInstanceRepository.findById(processInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Instance de processus non trouvée: " + processInstanceId));
        
        if (!"SUSPENDED".equals(instance.getStatus())) {
            throw new IllegalStateException("L'instance de processus n'est pas suspendue");
        }
        
        instance.setStatus("ACTIVE");
        instance.setUpdatedAt(LocalDateTime.now());
        
        return processInstanceRepository.save(instance);
    }

    @Override
    @Transactional
    public ProcessInstance terminateProcess(Long processInstanceId) {
        ProcessInstance instance = processInstanceRepository.findById(processInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Instance de processus non trouvée: " + processInstanceId));
        
        instance.setStatus("TERMINATED");
        instance.setEndTime(LocalDateTime.now());
        instance.setUpdatedAt(LocalDateTime.now());
        
        return processInstanceRepository.save(instance);
    }

    @Override
    @Transactional
    public BpmnProcess deployProcess(String processDefinition, String name) {
        // Analyser le processus BPMN
        // Ici, nous simulons la création du processus à partir de la définition XML
        BpmnProcess process = BpmnProcess.builder()
                .id(java.util.UUID.randomUUID().toString())
                .name(name)
                .isExecutable(true)
                .description("Processus déployé automatiquement")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // Persister le processus
        return bpmnProcessRepository.save(process);
    }
    
    // Méthodes utilitaires privées
    
    private String findStartElement(BpmnProcess process) {
        // Chercher d'abord un événement de démarrage
        Optional<Event> startEvent = process.getEvents().stream()
                .filter(event -> event.getTypeEvent().name().contains("START"))
                .findFirst();
        
        if (startEvent.isPresent()) {
            // Trouver la séquence sortant de l'événement de démarrage
            Optional<SequenceFlow> startSequence = process.getSequenceFlows().stream()
                    .filter(flow -> flow.getSourceRef().equals(startEvent.get().getId()))
                    .findFirst();
            
            if (startSequence.isPresent()) {
                return startSequence.get().getTargetRef();
            }
        }
        
        // Si pas d'événement de démarrage trouvé, prendre la première tâche
        if (process.getTasks() != null && !process.getTasks().isEmpty()) {
            return process.getTasks().get(0).getId();
        }
        
        return null;
    }
    
    private void verifyUserAuthorization(ProcessInstance instance, String taskId, User user) {
        // Récupérer la configuration de la tâche
        TaskConfiguration config = taskConfigurationRepository.findByTaskId(taskId).orElse(null);
        
        // Si pas de configuration, on autorise par défaut
        if (config == null) {
            return;
        }
        
        // Vérifier si l'utilisateur est autorisé
        boolean authorized = false;
        
        // Vérifier les rôles requis
        if (config.getRequiredRoles() != null && !config.getRequiredRoles().isEmpty()) {
            // Ici, on devrait vérifier si l'utilisateur a les rôles requis
            // Pour l'exemple, on suppose que l'utilisateur a les rôles requis
            authorized = true;
        }
        
        // Vérifier les groupes autorisés
        if (!authorized && config.getAuthorizedGroups() != null && !config.getAuthorizedGroups().isEmpty()) {
            // Ici, on devrait vérifier si l'utilisateur appartient aux groupes autorisés
            // Pour l'exemple, on suppose que l'utilisateur appartient aux groupes autorisés
            authorized = true;
        }
        
        if (!authorized) {
            throw new SecurityException("L'utilisateur n'est pas autorisé à effectuer cette tâche");
        }
    }
    
    private String getTaskName(BpmnProcess process, String taskId) {
        // Récupérer le nom de la tâche
        return process.getTasks().stream()
                .filter(task -> task.getId().equals(taskId))
                .map(Task::getName)
                .findFirst()
                .orElse("Tâche inconnue");
    }
    
    private List<String> determineNextElements(BpmnProcess process, String currentElementId, Map<String, Object> variables) {
        // Trouver les séquences sortant de l'élément courant
        List<SequenceFlow> outgoingFlows = process.getSequenceFlows().stream()
                .filter(flow -> flow.getSourceRef().equals(currentElementId))
                .collect(Collectors.toList());
        
        // Si pas de flux sortant, fin du processus
        if (outgoingFlows.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Si un seul flux sortant, le suivre directement
        if (outgoingFlows.size() == 1) {
            String targetRef = outgoingFlows.get(0).getTargetRef();
            return List.of(targetRef);
        }
        
        // S'il y a plusieurs flux sortants, c'est probablement une passerelle
        // Chercher la passerelle connectée
        for (SequenceFlow flow : outgoingFlows) {
            Optional<Gateway> gateway = process.getGateways().stream()
                    .filter(g -> g.getId().equals(flow.getTargetRef()))
                    .findFirst();
            
            if (gateway.isPresent()) {
                // Trouver les flux sortants de la passerelle
                List<SequenceFlow> gatewayOutflows = process.getSequenceFlows().stream()
                        .filter(f -> f.getSourceRef().equals(gateway.get().getId()))
                        .collect(Collectors.toList());
                
                // Évaluer la passerelle selon son type
                return evaluateGateway(gateway.get(), gatewayOutflows, variables).stream()
                        .map(SequenceFlow::getTargetRef)
                        .collect(Collectors.toList());
            }
        }
        
        // Si on ne trouve pas de passerelle, suivre le premier flux par défaut
        return List.of(outgoingFlows.get(0).getTargetRef());
    }
    
    private List<SequenceFlow> evaluateGateway(Gateway gateway, List<SequenceFlow> outgoingFlows, Map<String, Object> variables) {
        TypeGateway type = gateway.getTypeGateway();
        
        switch (type) {
            case EXCLUSIVE:
                return List.of(gatewayEvaluator.evaluateExclusiveGateway(gateway, outgoingFlows, variables));
            case INCLUSIVE:
                return gatewayEvaluator.evaluateInclusiveGateway(gateway, outgoingFlows, variables);
            case PARALLEL:
                return gatewayEvaluator.evaluateParallelGateway(gateway, outgoingFlows);
            case EVENT_BASED:
                // Pour les passerelles basées sur les événements, configuration spéciale
                setupEventBasedGateway(gateway, outgoingFlows);
                // Retourner un flux vide car le processus sera repris quand un événement sera déclenché
                return new ArrayList<>();
            default:
                log.warn("Type de passerelle non géré: {}. Traitement comme une passerelle exclusive.", type);
                return List.of(gatewayEvaluator.evaluateExclusiveGateway(gateway, outgoingFlows, variables));
        }
    }
    
    private void setupInitialEvents(ProcessInstance instance) {
        BpmnProcess process = instance.getProcess();
        
        // Chercher tous les événements qui peuvent déclencher le processus (timer, message, signal)
        process.getEvents().stream()
                .filter(event -> event.getTypeEvent().name().contains("START"))
                .forEach(event -> {
                    if (event.getTriggerType() == TriggerType.TIMER) {
                        eventHandler.scheduleTimerEvent(instance, event, instance.getProcessVariables());
                    } else if (event.getTriggerType() == TriggerType.MESSAGE) {
                        // Pour les messages, on utiliserait le businessKey comme clé de corrélation
                        eventHandler.subscribeToMessage(instance, event, instance.getBusinessKey());
                    } else if (event.getTriggerType() == TriggerType.SIGNAL) {
                        eventHandler.subscribeToSignal(instance, event);
                    }
                });
    }
    
    private void setupEventIfNeeded(ProcessInstance instance, String elementId) {
        BpmnProcess process = instance.getProcess();
        
        // Chercher si l'élément est un événement
        Optional<Event> event = process.getEvents().stream()
                .filter(e -> e.getId().equals(elementId))
                .findFirst();
        
        if (event.isPresent()) {
            Event e = event.get();
            
            if (e.getTriggerType() == TriggerType.TIMER) {
                eventHandler.scheduleTimerEvent(instance, e, instance.getProcessVariables());
            } else if (e.getTriggerType() == TriggerType.MESSAGE) {
                // Pour les messages, utiliser le businessKey comme clé de corrélation
                eventHandler.subscribeToMessage(instance, e, instance.getBusinessKey());
            } else if (e.getTriggerType() == TriggerType.SIGNAL) {
                eventHandler.subscribeToSignal(instance, e);
            }
        }
    }
    
    private void setupEventBasedGateway(Gateway gateway, List<SequenceFlow> outgoingFlows) {
        // Pour chaque flux sortant, trouver l'événement associé et le configurer
        for (SequenceFlow flow : outgoingFlows) {
            String targetRef = flow.getTargetRef();
            
            // Chercher l'instance de processus concernée
            // Dans une implémentation réelle, cela serait géré par le moteur de workflow
            log.info("Configuration de la passerelle basée sur les événements pour la cible {}", targetRef);
            
            // Ici, nous simulons simplement le comportement
            // En production, cela serait fait par le moteur Camunda
        }
    }
    
    @EventListener
    public void handleTimerEvent(TimerEvent event) {
        log.info("Réception d'un événement timer pour l'instance {} et l'événement {}", 
                event.getProcessInstanceId(), event.getEventId());
        
        // Récupérer l'instance de processus
        Optional<ProcessInstance> optInstance = processInstanceRepository.findById(event.getProcessInstanceId());
        
        if (optInstance.isPresent()) {
            ProcessInstance instance = optInstance.get();
            
            // Si le processus est actif et l'événement correspond à l'élément courant
            if ("ACTIVE".equals(instance.getStatus()) && event.getEventId().equals(instance.getCurrentTaskId())) {
                // Construire un utilisateur système pour l'exécution automatique
                User systemUser = User.builder()
                        .id(0L)
                        .username("system")
                        .build();
                
                // Compléter la tâche/événement
                completeTask(instance.getId(), event.getEventId(), systemUser, event.getVariables());
            }
        }
    }
    
    @EventListener
    public void handleMessageEvent(MessageEvent event) {
        log.info("Réception d'un message: {} avec la clé {}", 
                event.getMessageName(), event.getCorrelationKey());
        
        // Chercher toutes les souscriptions correspondantes
        // Dans une implémentation réelle, cela serait géré par le moteur de workflow
        
        // Ici, nous simulons simplement le comportement
        // En production, cela serait fait par le moteur Camunda
    }
    
    @EventListener
    public void handleSignalEvent(SignalEvent event) {
        log.info("Réception d'un signal: {}", event.getSignalName());
        
        // Chercher toutes les souscriptions correspondantes
        // Dans une implémentation réelle, cela serait géré par le moteur de workflow
        
        // Ici, nous simulons simplement le comportement
        // En production, cela serait fait par le moteur Camunda
    }

    @Override
    @Transactional
    public boolean triggerEvent(Long processInstanceId, String eventId, Map<String, Object> variables) {
        log.info("Déclenchement de l'événement {} dans l'instance {}", eventId, processInstanceId);
        
        try {
            // Récupérer l'instance de processus
            ProcessInstance instance = processInstanceRepository.findById(processInstanceId)
                    .orElseThrow(() -> new IllegalArgumentException("Instance de processus non trouvée: " + processInstanceId));
            
            // Vérifier que l'instance est active
            if (!"ACTIVE".equals(instance.getStatus())) {
                log.warn("Impossible de déclencher l'événement: l'instance {} n'est pas active (statut: {})",
                        processInstanceId, instance.getStatus());
                return false;
            }
            
            // Récupérer le processus BPMN
            BpmnProcess process = instance.getProcess();
            
            // Trouver l'événement dans le processus
            Event event = process.getEvents().stream()
                    .filter(e -> e.getId().equals(eventId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Événement non trouvé: " + eventId));
            
            // Mettre à jour les variables du processus
            if (variables != null && !variables.isEmpty()) {
                Map<String, Object> processVariables = instance.getProcessVariables();
                processVariables.putAll(variables);
                instance.setProcessVariables(processVariables);
            }
            
            // Trouver la séquence sortant de l'événement
            List<SequenceFlow> outgoingFlows = process.getSequenceFlows().stream()
                    .filter(flow -> flow.getSourceRef().equals(eventId))
                    .collect(Collectors.toList());
            
            if (outgoingFlows.isEmpty()) {
                log.warn("Aucun flux sortant trouvé pour l'événement {}", eventId);
                return false;
            }
            
            // Choisir le premier flux sortant
            SequenceFlow nextFlow = outgoingFlows.get(0);
            String nextElementId = nextFlow.getTargetRef();
            
            // Convertir les variables au format JSON
            String inputVarsJson = null;
            if (variables != null && !variables.isEmpty()) {
                try {
                    inputVarsJson = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(variables);
                } catch (Exception e) {
                    log.warn("Impossible de sérialiser les variables en JSON", e);
                }
            }
            
            // Enregistrer l'exécution de l'événement
            LocalDateTime now = LocalDateTime.now();
            ProcessExecution execution = ProcessExecution.builder()
                    .processInstanceId(processInstanceId)
                    .taskId(eventId)
                    .taskName(event.getName() != null ? event.getName() : "Event: " + eventId)
                    .status("COMPLETED")
                    .startTime(now)
                    .endTime(now)
                    .durationInMillis(0L)
                    .inputVariables(inputVarsJson)
                    .result("EVENT_TRIGGERED")
                    .createdAt(now)
                    .build();
            
            processExecutionRepository.save(execution);
            
            // Mettre à jour l'instance avec la nouvelle tâche courante
            instance.setCurrentTaskId(nextElementId);
            instance.setUpdatedAt(LocalDateTime.now());
            instance.getExecutionHistory().add(execution);
            
            // Persister les modifications
            processInstanceRepository.save(instance);
            
            log.info("Événement {} déclenché avec succès dans l'instance {}, prochaine tâche: {}",
                    eventId, processInstanceId, nextElementId);
            
            return true;
        } catch (Exception e) {
            log.error("Erreur lors du déclenchement de l'événement {} dans l'instance {}: {}",
                    eventId, processInstanceId, e.getMessage(), e);
            return false;
        }
    }
} 