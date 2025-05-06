package com.harmony.harmoniservices.core.domain.entities;

import lombok.*;

import java.time.LocalDateTime;

import com.harmony.harmoniservices.core.domain.enums.RoleUser;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phone;
    private RoleUser role;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
}