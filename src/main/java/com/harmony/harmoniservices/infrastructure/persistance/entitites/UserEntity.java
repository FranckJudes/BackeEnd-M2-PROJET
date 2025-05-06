package com.harmony.harmoniservices.infrastructure.persistance.entitites;

import lombok.*;

import com.harmony.harmoniservices.core.domain.enums.UserStatus;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
public class UserEntity {
    
    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String username;

    private String email;
    private String phone;

    @Column(nullable = true)
    private String profilePicture;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserStatus status =  UserStatus.ACTIVE;

    @Column(nullable = true)
    private String role;

    @Column(nullable = true)
    private String theme;
    
    @Column(nullable = true)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime updatedAt;
}
