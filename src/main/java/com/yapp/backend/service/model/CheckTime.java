package com.yapp.backend.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckTime {
    private String checkInTimeFrom;
    private String checkInTimeTo;
    private String checkOutTimeFrom;
    private String checkOutTimeTo;
}
