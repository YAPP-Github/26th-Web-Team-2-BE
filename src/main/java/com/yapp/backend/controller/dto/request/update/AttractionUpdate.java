package com.yapp.backend.controller.dto.request.update;

import com.yapp.backend.service.model.DistanceInfo;
import lombok.Getter;

@Getter
public class AttractionUpdate {
    private String name;
    private String type;
    private String distance;
    private Double latitude;
    private Double longitude;
    private DistanceInfo byFoot;
    private DistanceInfo byCar;
}
