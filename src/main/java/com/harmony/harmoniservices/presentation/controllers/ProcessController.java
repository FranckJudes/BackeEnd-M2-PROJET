package com.harmony.harmoniservices.presentation.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.harmony.harmoniservices.core.domain.entities.BpmnProcess;
import com.harmony.harmoniservices.core.domain.entities.ProcessInstance;
import com.harmony.harmoniservices.core.domain.entities.TaskConfiguration;
import com.harmony.harmoniservices.core.domain.entities.User;
import com.harmony.harmoniservices.core.ports.cases.ProcessAutomationService;
import com.harmony.harmoniservices.core.ports.cases.UserService;
import com.harmony.harmoniservices.presentation.dto.responses.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/process")
@RequiredArgsConstructor
public class ProcessController {

    private final ProcessAutomationService processAutomationService;
    private final UserService userService;

    /**
     * Déploie un nouveau processus BPMN
     */
    @PostMapping("/deploy")
    public ResponseEntity<ApiResponse<BpmnProcess>> deployProcess(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name) {
        try {
            String processDefinition = new String(file.getBytes());
            BpmnProcess process = processAutomationService.deployProcess(processDefinition, name);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Processus déployé avec succès", process));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail("Erreur lors du déploiement du processus: " + e.getMessage()));
        }
    }

    /**
     * Démarre une nouvelle instance de processus
     */
    @PostMapping("/{processId}/start")
    public ResponseEntity<ApiResponse<ProcessInstance>> startProcess(
            @PathVariable("processId") String processId,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "businessKey", required = false) String businessKey,
            @RequestBody(required = false) Map<String, Object> variables) {
        try {
            User user = userService.findUser(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé: " + userId));
            
            ProcessInstance instance = processAutomationService.startProcess(processId, user, variables, businessKey);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Instance de processus démarrée avec succès", instance));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail("Erreur lors du démarrage du processus: " + e.getMessage()));
        }
    }

    /**
     * Complète une tâche dans une instance de processus
     */
    @PostMapping("/instance/{instanceId}/task/{taskId}/complete")
    public ResponseEntity<ApiResponse<ProcessInstance>> completeTask(
            @PathVariable("instanceId") Long instanceId,
            @PathVariable("taskId") String taskId,
            @RequestParam("userId") Long userId,
            @RequestBody(required = false) Map<String, Object> variables) {
        try {
            User user = userService.findUser(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé: " + userId));
            
            ProcessInstance instance = processAutomationService.completeTask(instanceId, taskId, user, variables);
            return ResponseEntity.ok(ApiResponse.success("Tâche complétée avec succès", instance));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail("Erreur lors de l'achèvement de la tâche: " + e.getMessage()));
        }
    }

    /**
     * Récupère les tâches assignées à un utilisateur
     */
    @GetMapping("/tasks/user/{userId}")
    public ResponseEntity<ApiResponse<List<ProcessInstance>>> getUserTasks(@PathVariable("userId") Long userId) {
        try {
            List<ProcessInstance> instances = processAutomationService.getUserTasks(userId);
            return ResponseEntity.ok(ApiResponse.success("Tâches utilisateur récupérées avec succès", instances));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Erreur lors de la récupération des tâches utilisateur: " + e.getMessage()));
        }
    }

    /**
     * Récupère les tâches disponibles pour un groupe
     */
    @GetMapping("/tasks/group/{groupId}")
    public ResponseEntity<ApiResponse<List<ProcessInstance>>> getGroupTasks(@PathVariable("groupId") Long groupId) {
        try {
            List<ProcessInstance> instances = processAutomationService.getGroupTasks(groupId);
            return ResponseEntity.ok(ApiResponse.success("Tâches groupe récupérées avec succès", instances));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Erreur lors de la récupération des tâches groupe: " + e.getMessage()));
        }
    }

    /**
     * Récupère une instance de processus
     */
    @GetMapping("/instance/{instanceId}")
    public ResponseEntity<ApiResponse<ProcessInstance>> getProcessInstance(@PathVariable("instanceId") Long instanceId) {
        try {
            return processAutomationService.getProcessInstance(instanceId)
                    .map(instance -> ResponseEntity.ok(ApiResponse.success("Instance de processus récupérée avec succès", instance)))
                    .orElse(ResponseEntity
                            .status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.fail("Instance de processus non trouvée: " + instanceId)));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Erreur lors de la récupération de l'instance de processus: " + e.getMessage()));
        }
    }

    /**
     * Configure une tâche dans un processus
     */
    @PostMapping("/{processId}/task/{taskId}/configure")
    public ResponseEntity<ApiResponse<TaskConfiguration>> configureTask(
            @PathVariable("processId") String processId,
            @PathVariable("taskId") String taskId,
            @RequestBody TaskConfiguration configuration) {
        try {
            TaskConfiguration savedConfig = processAutomationService.configureTask(processId, taskId, configuration);
            return ResponseEntity.ok(ApiResponse.success("Tâche configurée avec succès", savedConfig));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail("Erreur lors de la configuration de la tâche: " + e.getMessage()));
        }
    }

    /**
     * Récupère la configuration d'une tâche
     */
    @GetMapping("/{processId}/task/{taskId}/configuration")
    public ResponseEntity<ApiResponse<TaskConfiguration>> getTaskConfiguration(
            @PathVariable("processId") String processId,
            @PathVariable("taskId") String taskId) {
        try {
            return processAutomationService.getTaskConfiguration(processId, taskId)
                    .map(config -> ResponseEntity.ok(ApiResponse.success("Configuration de tâche récupérée avec succès", config)))
                    .orElse(ResponseEntity
                            .status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.fail("Configuration de tâche non trouvée")));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Erreur lors de la récupération de la configuration de tâche: " + e.getMessage()));
        }
    }

    /**
     * Suspend une instance de processus
     */
    @PostMapping("/instance/{instanceId}/suspend")
    public ResponseEntity<ApiResponse<ProcessInstance>> suspendProcess(@PathVariable("instanceId") Long instanceId) {
        try {
            ProcessInstance instance = processAutomationService.suspendProcess(instanceId);
            return ResponseEntity.ok(ApiResponse.success("Instance de processus suspendue avec succès", instance));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail("Erreur lors de la suspension de l'instance de processus: " + e.getMessage()));
        }
    }

    /**
     * Reprend une instance de processus suspendue
     */
    @PostMapping("/instance/{instanceId}/resume")
    public ResponseEntity<ApiResponse<ProcessInstance>> resumeProcess(@PathVariable("instanceId") Long instanceId) {
        try {
            ProcessInstance instance = processAutomationService.resumeProcess(instanceId);
            return ResponseEntity.ok(ApiResponse.success("Instance de processus reprise avec succès", instance));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail("Erreur lors de la reprise de l'instance de processus: " + e.getMessage()));
        }
    }

    /**
     * Arrête une instance de processus
     */
    @PostMapping("/instance/{instanceId}/terminate")
    public ResponseEntity<ApiResponse<ProcessInstance>> terminateProcess(@PathVariable("instanceId") Long instanceId) {
        try {
            ProcessInstance instance = processAutomationService.terminateProcess(instanceId);
            return ResponseEntity.ok(ApiResponse.success("Instance de processus arrêtée avec succès", instance));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail("Erreur lors de l'arrêt de l'instance de processus: " + e.getMessage()));
        }
    }
} 