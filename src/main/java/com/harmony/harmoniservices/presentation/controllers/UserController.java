package com.harmony.harmoniservices.presentation.controllers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.harmony.harmoniservices.core.domain.entities.User;
import com.harmony.harmoniservices.core.ports.cases.UserService;
import com.harmony.harmoniservices.presentation.dto.responses.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userSyncService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        try {
            List<User> users = userSyncService.getAll();
            return ResponseEntity.ok(ApiResponse.success("Liste des utilisateurs récupérée avec succès", users));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("Erreur lors de la récupération des utilisateurs: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> findOneUser(@PathVariable Long id) {
        try {
            return userSyncService.findUser(id)
                .map(user -> ResponseEntity.ok(ApiResponse.success("Utilisateur trouvé avec succès", user)))
                .orElse(ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("Utilisateur avec id " + id + " non trouvé")));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("Erreur lors de la recherche de l'utilisateur: " + e.getMessage()));
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User user) {
        try {
            User createdUser = userSyncService.createUser(user);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Utilisateur créé avec succès", createdUser));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail("Erreur lors de la création de l'utilisateur: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            // S'assurer que l'ID dans le chemin correspond à l'ID dans l'objet user
            if (user.getId() == null) {
                user.setId(id);
            } else if (!user.getId().equals(id)) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail("L'ID dans le chemin ne correspond pas à l'ID dans l'objet utilisateur"));
            }
            
            // Vérifier si l'utilisateur existe
            if (userSyncService.findUser(id).isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("Utilisateur avec id " + id + " non trouvé"));
            }
            
            User updatedUser = userSyncService.updateUser(user);
            return ResponseEntity.ok(ApiResponse.success("Utilisateur mis à jour avec succès", updatedUser));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        try {
            // Vérifier si l'utilisateur existe
            if (userSyncService.findUser(id).isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("Utilisateur avec id " + id + " non trouvé"));
            }
            
            userSyncService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("Utilisateur supprimé avec succès"));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("Erreur lors de la suppression de l'utilisateur: " + e.getMessage()));
        }
    }
}
