package com.yapp.backend.controller.dto.request.update;

import com.yapp.backend.service.model.DistanceInfo;
import lombok.Getter;

@Getter
public class AttractionUpdate {
    private String name;
    private String distance;
    private DistanceInfo byFoot;
    private DistanceInfo byCar;
}
