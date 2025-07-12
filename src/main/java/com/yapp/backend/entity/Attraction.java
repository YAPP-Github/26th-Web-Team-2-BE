package com.yapp.backend.entity;

import lombok.Getter;

@Getter
public class Attraction {
    private String name;
    private String type;
    private Double latitude;
    private Double longitude;
    private DistanceInfo byFoot;
    private DistanceInfo byCar;
}
