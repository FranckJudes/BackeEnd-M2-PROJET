package com.harmony.harmoniservices.presentation.controllers;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.harmony.harmoniservices.core.ports.cases.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userSyncService;

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userSyncService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> FindOneUser(@PathVariable Long id) {
        return ResponseEntity.ok(userSyncService.findUser(id));
    }
}
