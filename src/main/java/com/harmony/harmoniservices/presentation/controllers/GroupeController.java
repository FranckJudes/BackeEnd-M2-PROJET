package com.harmony.harmoniservices.presentation.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.harmony.harmoniservices.core.domain.entities.GroupeUtilisateur;
import com.harmony.harmoniservices.core.ports.cases.GroupeUtilisateurService;
import com.harmony.harmoniservices.presentation.dto.Requests.GroupeRequest;


import jakarta.validation.Valid;

@RestController
@RequestMapping("/groupe")
public class GroupeController {

    private final GroupeUtilisateurService groupeUtilisateurService;
    public GroupeController(GroupeUtilisateurService groupeUtilisateurService) {
        this.groupeUtilisateurService = groupeUtilisateurService;
    }
    
    @GetMapping("")
    public ResponseEntity<?> getAllGroups() {
        return ResponseEntity.ok(groupeUtilisateurService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGroupById(@PathVariable Long id) {
        return ResponseEntity.ok(groupeUtilisateurService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long id) {
        groupeUtilisateurService.delete(id);
        return ResponseEntity.ok("Group deleted successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGroup(@PathVariable Long id, @RequestBody @Valid GroupeRequest groupDto) {
        GroupeUtilisateur group = new GroupeUtilisateur();
        group.setLibeleGroupeUtilisateur(groupDto.name());
        group.setDescriptionGroupeUtilisateur(groupDto.description());
        group.setType(groupDto.type());
        groupeUtilisateurService.update(id, group);
        return ResponseEntity.ok("Group updated successfully");
    }

    @PostMapping()
    public ResponseEntity<?> createGroup(@RequestBody @Valid GroupeRequest groupDto) {
        GroupeUtilisateur group = new GroupeUtilisateur();
        group.setLibeleGroupeUtilisateur(groupDto.name());
        group.setDescriptionGroupeUtilisateur(groupDto.description());
        group.setType(groupDto.type());
        groupeUtilisateurService.create(group);
        return ResponseEntity.ok("Group created successfully");
    }
    
}
