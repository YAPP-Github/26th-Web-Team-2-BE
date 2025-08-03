package com.yapp.backend.controller.dto.request;

import com.yapp.backend.controller.dto.request.update.AmenityUpdate;
import com.yapp.backend.controller.dto.request.update.AttractionUpdate;
import com.yapp.backend.controller.dto.request.update.CheckTimeUpdate;
import com.yapp.backend.controller.dto.request.update.TransportationUpdate;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;

@Getter
public class UpdateAccommodationRequest {
    private Long id;

    @Size(max = 100, message = "100자 이내로 수정 가능합니다.")
    private String memo;
    private Integer lowestPrice;
    private String currency;
    private Double reviewScore;
    private Double cleanlinessScore;

    @Size(max = 100, message = "100자 이내로 수정 가능합니다.")
    private String reviewSummary;
    private List<AttractionUpdate> nearbyAttractions;
    private List<TransportationUpdate> nearbyTransportation;
    private List<AmenityUpdate> amenities;
    private CheckTimeUpdate checkInTime;
    private CheckTimeUpdate checkOutTime;
}
