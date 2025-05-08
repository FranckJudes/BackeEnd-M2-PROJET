package com.harmony.harmoniservices.core.cases;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.harmony.harmoniservices.core.domain.entities.BpmnProcess;
import com.harmony.harmoniservices.core.domain.entities.ProcessExecution;
import com.harmony.harmoniservices.core.domain.entities.ProcessInstance;
import com.harmony.harmoniservices.core.domain.entities.Task;
import com.harmony.harmoniservices.core.domain.entities.TaskConfiguration;
import com.harmony.harmoniservices.core.domain.entities.User;
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
        
        // Déterminer la première tâche
        // Ici, on suppose que la première tâche est identifiée par une logique métier
        // En pratique, vous utiliseriez un moteur de workflow comme Camunda ou Flowable pour déterminer cela
        
        // Exemple simplifié: prendre la première tâche trouvée
        String firstTaskId = findFirstTaskId(process);
        instance.setCurrentTaskId(firstTaskId);
        
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
        
        // Déterminer la prochaine tâche
        String nextTaskId = determineNextTask(instance.getProcess(), taskId, instance.getProcessVariables());
        
        // Si pas de prochaine tâche, terminer le processus
        if (nextTaskId == null) {
            instance.setStatus("COMPLETED");
            instance.setEndTime(now);
            instance.setCurrentTaskId(null);
        } else {
            instance.setCurrentTaskId(nextTaskId);
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
    
    private String findFirstTaskId(BpmnProcess process) {
        // Ici, on devrait utiliser la logique du moteur de workflow pour déterminer la première tâche
        // Pour l'exemple, on prend simplement la première tâche trouvée
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
    
    private String determineNextTask(BpmnProcess process, String currentTaskId, Map<String, Object> variables) {
        // Ici, on devrait utiliser la logique du moteur de workflow pour déterminer la prochaine tâche
        // Pour l'exemple, on prend simplement la tâche suivante dans la liste
        List<Task> tasks = process.getTasks();
        if (tasks == null || tasks.isEmpty()) {
            return null;
        }
        
        int currentIndex = -1;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId().equals(currentTaskId)) {
                currentIndex = i;
                break;
            }
        }
        
        if (currentIndex >= 0 && currentIndex < tasks.size() - 1) {
            return tasks.get(currentIndex + 1).getId();
        }
        
        return null; // Pas de tâche suivante, fin du processus
    }
} 