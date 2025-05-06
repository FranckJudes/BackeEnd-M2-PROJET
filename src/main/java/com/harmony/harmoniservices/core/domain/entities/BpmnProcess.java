package com.harmony.harmoniservices.core.domain.entities;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BpmnProcess {
    private String id;
    private String name;
    private Boolean isExecutable;
    private String description;
    private String keywords;
    private String imagePaths;
    private String filePaths;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}