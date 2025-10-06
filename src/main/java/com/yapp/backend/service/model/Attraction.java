package com.yapp.backend.service.model;

import com.yapp.backend.controller.dto.request.update.AttractionUpdate;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Attraction {
    private String name;
    private String type;
    private Double latitude;
    private Double longitude;
    private String distance;
    private DistanceInfo byFoot;
    private DistanceInfo byCar;

    public Attraction update(AttractionUpdate updates) {
        this.name = updates.getName();
        this.distance = updates.getDistance();
        this.byFoot = updates.getByFoot();
        this.byCar = updates.getByCar();
        return this;
    }
}
