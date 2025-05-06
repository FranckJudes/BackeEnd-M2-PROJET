package com.harmony.harmoniservices.core.domain.entities;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pool {
    private String id;
    private String name;
    private BpmnProcess process;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}