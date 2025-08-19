package com.yapp.backend.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yapp.backend.service.model.DistanceInfo;
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
    @JsonProperty("ByFoot")
    private DistanceInfo byFoot;
    @JsonProperty("ByCar")
    private DistanceInfo byCar;

}