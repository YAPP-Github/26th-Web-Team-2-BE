package com.yapp.backend.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripGroup {
    private Long groupId;
    private String groupName;
    private User createdBy;
    private User updatedBy;
}
