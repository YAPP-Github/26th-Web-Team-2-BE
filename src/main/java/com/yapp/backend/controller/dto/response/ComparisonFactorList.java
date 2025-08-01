package com.yapp.backend.controller.dto.response;

import com.yapp.backend.service.model.enums.ComparisonFactor;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ComparisonFactorList {
    List<ComparisonFactor> factors;

}
