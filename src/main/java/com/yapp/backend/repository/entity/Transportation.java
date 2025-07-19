package com.yapp.backend.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transportation {
    private String name;
    private String type;
    private Double latitude;
    private Double longitude;
    private String distance;
    private DistanceInfo byFoot;
    private DistanceInfo byCar;
}
