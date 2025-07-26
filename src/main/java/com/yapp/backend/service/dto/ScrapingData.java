package com.yapp.backend.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScrapingData {
    private String url;
    
    @JsonProperty("site_name")
    private String siteName;
    
    @JsonProperty("accommodation_name")
    private String accommodationName;
    
    private List<String> image;
    
    private String address;
    
    private Double latitude;
    
    private Double longitude;
    
    @JsonProperty("lowest_price")
    private String lowestPrice;
    
    @JsonProperty("highest_price")
    private String highestPrice;
    
    private String currency;
    
    @JsonProperty("review_score")
    private Double reviewScore;
    
    @JsonProperty("cleanliness_score")
    private Double cleanlinessScore;
    
    @JsonProperty("review_summary")
    private String reviewSummary;
    
    @JsonProperty("nearby_attractions")
    private List<ScrapingAttraction> nearbyAttractions;
    
    @JsonProperty("nearby_transportation")
    private List<ScrapingTransportation> nearbyTransportation;
    
    private List<ScrapingAmenity> amenities;
    
    @JsonProperty("check_in_time")
    private ScrapingCheckTime checkInTime;
    
    @JsonProperty("check_out_time")
    private ScrapingCheckTime checkOutTime;
    
    @JsonProperty("hotel_id")
    private String hotelId;
}