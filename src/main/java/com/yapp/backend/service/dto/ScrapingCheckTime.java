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
public class ScrapingCheckTime {
    @JsonProperty("check_in_time_from")
    private String checkInTimeFrom;
    
    @JsonProperty("check_in_time_to")
    private String checkInTimeTo;
    
    @JsonProperty("check_out_time_from")
    private String checkOutTimeFrom;
    
    @JsonProperty("check_out_time_to")
    private String checkOutTimeTo;
}