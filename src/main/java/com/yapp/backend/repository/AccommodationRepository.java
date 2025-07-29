package com.yapp.backend.repository;

import com.yapp.backend.service.model.Accommodation;
import com.yapp.backend.repository.entity.AccommodationEntity;

import java.util.List;

public interface AccommodationRepository {

    /**
     * 테이블 ID로 숙소 목록을 페이징하여 조회하는 쿼리
     * userId가 null이 아닌 경우 해당 사용자가 생성한 숙소만 조회
     */
    List<Accommodation> findByTableIdWithPagination(Long tableId, int page, int size, Long userId);

    /**
     * 테이블 ID로 숙소 개수를 조회
     * userId가 null이 아닌 경우 해당 사용자가 생성한 숙소 개수만 조회
     */
    Long countByTableId(Long tableId, Long userId);

    /**
     * 숙소를 저장합니다.
     */
    Accommodation save(AccommodationEntity accommodationEntity);

    /**
     * 숙소 ID로 단건 조회합니다.
     */
    Accommodation findById(Long accommodationId);
}
