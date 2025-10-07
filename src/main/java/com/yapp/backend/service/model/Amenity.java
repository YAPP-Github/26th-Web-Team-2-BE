package com.yapp.backend.service.model;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Amenity {
    private String type;
    private boolean available;
    private String description;
}
