package com.yapp.backend.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScrapingAttraction {
    private String name;
    private String distance;
    private String type;
    private Double latitude;
    private Double longitude;
}