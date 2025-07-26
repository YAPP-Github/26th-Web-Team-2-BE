package com.yapp.backend.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScrapingAmenity {
    private String type;
    
    @JsonProperty("isAvailable")
    private boolean available;
    
    private String description;
}