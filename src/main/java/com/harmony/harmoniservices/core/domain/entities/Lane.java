package com.harmony.harmoniservices.core.domain.entities;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lane {
    private String id;
    private String name;
    private LaneSet laneSet;
}