package com.harmony.harmoniservices.presentation.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.harmony.harmoniservices.core.domain.entities.GroupeUtilisateur;
import com.harmony.harmoniservices.core.ports.cases.GroupeUtilisateurService;
import com.harmony.harmoniservices.presentation.dto.Requests.GroupeRequest;
import com.harmony.harmoniservices.presentation.dto.responses.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/groupe")
public class GroupeController {

    private final GroupeUtilisateurService groupeUtilisateurService;
    public GroupeController(GroupeUtilisateurService groupeUtilisateurService) {
        this.groupeUtilisateurService = groupeUtilisateurService;
    }
    
    @GetMapping("")
    public ResponseEntity<ApiResponse<List<GroupeUtilisateur>>> getAllGroups() {
        try {
            List<GroupeUtilisateur> groups = groupeUtilisateurService.getAll();
            return ResponseEntity.ok(ApiResponse.success("Liste des groupes récupérée avec succès", groups));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("Erreur lors de la récupération des groupes: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GroupeUtilisateur>> getGroupById(@PathVariable Long id) {
        try {
            GroupeUtilisateur group = groupeUtilisateurService.getById(id);
            if (group != null) {
                return ResponseEntity.ok(ApiResponse.success("Groupe trouvé avec succès", group));
            } else {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("Groupe avec id " + id + " non trouvé"));
            }
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("Erreur lors de la recherche du groupe: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(@PathVariable Long id) {
        try {
            GroupeUtilisateur group = groupeUtilisateurService.getById(id);
            if (group == null) {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("Groupe avec id " + id + " non trouvé"));
            }
            
            groupeUtilisateurService.delete(id);
            return ResponseEntity.ok(ApiResponse.success("Groupe supprimé avec succès"));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("Erreur lors de la suppression du groupe: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GroupeUtilisateur>> updateGroup(@PathVariable Long id, @RequestBody @Valid GroupeRequest groupDto) {
        try {
            // Vérifier si le groupe existe
            GroupeUtilisateur existingGroup = groupeUtilisateurService.getById(id);
            if (existingGroup == null) {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("Groupe avec id " + id + " non trouvé"));
            }
            
            GroupeUtilisateur group = new GroupeUtilisateur();
            group.setLibeleGroupeUtilisateur(groupDto.name());
            group.setDescriptionGroupeUtilisateur(groupDto.description());
            group.setType(groupDto.type());
            
            GroupeUtilisateur updatedGroup = groupeUtilisateurService.update(id, group);
            return ResponseEntity.ok(ApiResponse.success("Groupe mis à jour avec succès", updatedGroup));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail("Erreur lors de la mise à jour du groupe: " + e.getMessage()));
        }
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<GroupeUtilisateur>> createGroup(@RequestBody @Valid GroupeRequest groupDto) {
        try {
            GroupeUtilisateur group = new GroupeUtilisateur();
            group.setLibeleGroupeUtilisateur(groupDto.name());
            group.setDescriptionGroupeUtilisateur(groupDto.description());
            group.setType(groupDto.type());
            
            GroupeUtilisateur createdGroup = groupeUtilisateurService.create(group);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Groupe créé avec succès", createdGroup));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail("Erreur lors de la création du groupe: " + e.getMessage()));
        }
    }
}
