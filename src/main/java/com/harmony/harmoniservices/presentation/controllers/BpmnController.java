package com.harmony.harmoniservices.presentation.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.harmony.harmoniservices.core.domain.entities.BpmnData;
import com.harmony.harmoniservices.core.ports.cases.BpmnService;

@RestController
@RequestMapping("/bpmn")

public class BpmnController {

    private final BpmnService bpmnService;

    public BpmnController(BpmnService bpmnService) {
        this.bpmnService = bpmnService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadBpmnFile(@RequestParam("file") MultipartFile file) {
        try {
            BpmnData bpmnData = bpmnService.parseBpmnFile(file.getInputStream());
            return ResponseEntity.ok(bpmnData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing BPMN file: " + e.getMessage());
        }
    }
    
}
