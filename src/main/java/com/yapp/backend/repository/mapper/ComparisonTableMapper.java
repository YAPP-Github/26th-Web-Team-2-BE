package com.yapp.backend.repository.mapper;

import com.yapp.backend.repository.JpaTripBoardRepository;
import com.yapp.backend.repository.JpaUserRepository;
import com.yapp.backend.repository.entity.AccommodationEntity;
import com.yapp.backend.repository.entity.ComparisonAccommodationEntity;
import com.yapp.backend.repository.entity.ComparisonTableEntity;
import com.yapp.backend.repository.entity.TripBoardEntity;
import com.yapp.backend.repository.entity.UserEntity;
import com.yapp.backend.service.model.Accommodation;
import com.yapp.backend.service.model.ComparisonTable;
import java.util.ArrayList;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ComparisonTableMapper {

    private final JpaTripBoardRepository jpaTripBoardRepository;
    private final JpaUserRepository jpaUserRepository;
    private final AccommodationMapper accommodationMapper;
    /**
     * 비교테이블 도메인 모델 -> 비교테이블 데이터 모델
     * @param comparisonTable
     * @return
     */
    public ComparisonTableEntity domainToEntity(ComparisonTable comparisonTable) {
        TripBoardEntity tripBoardProxy   = jpaTripBoardRepository.getReferenceById(comparisonTable.getTripBoardId());
        UserEntity      createdByProxy   = jpaUserRepository.getReferenceById(comparisonTable.getCreatedById());

        ComparisonTableEntity tableEntity = ComparisonTableEntity.builder()
                .tableName(comparisonTable.getTableName())
                .tripBoardEntity(tripBoardProxy)
                .createdByEntity(createdByProxy)
                .items(new ArrayList<>())
                .factors(comparisonTable.getFactors())
                .build();
        for (int i = 0; i < comparisonTable.getAccommodationList().size(); i++) {
            Accommodation accommodation = comparisonTable.getAccommodationList().get(i);
            AccommodationEntity accEntity = accommodationMapper.domainToEntity(accommodation);
            ComparisonAccommodationEntity itemEntity = ComparisonAccommodationEntity.builder()
                    .accommodationEntity(accEntity)
                    .position(i).build();
            itemEntity.setComparisonTable(tableEntity);
            tableEntity.getItems().add(itemEntity);
        }

        return tableEntity;
    }

    /**
     * 비교테이블 데이터 모델 -> 비교테이블 도메인 모델
     * @return
     */
    public ComparisonTable entityToDomain(ComparisonTableEntity entity) {
        return ComparisonTable.builder()
                .id(entity.getId())
                .tableName(entity.getTableName())
                .tripBoardId(entity.getTripBoardEntity().getId())
                .createdById(entity.getCreatedByEntity().getId())
                .accommodationList(
                        entity.getItems().stream()
                                .map(item -> accommodationMapper.entityToDomain(item.getAccommodationEntity())).collect(
                                        Collectors.toList()))
                .factors(entity.getFactors())
                .shareCode(entity.getShareCode())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }


}
