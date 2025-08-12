package com.yapp.backend.repository;

import com.yapp.backend.service.model.Accommodation;
import com.yapp.backend.repository.entity.AccommodationEntity;

import java.util.List;

public interface AccommodationRepository {

    /**
     * 여행보드 ID로 숙소 목록을 페이징하여 조회하는 쿼리
     * userId가 null이 아닌 경우 해당 사용자가 생성한 숙소만 조회
     * sort 파라미터에 따라 정렬 방식 결정 (saved_at_desc: 최근 등록순, price_asc: 가격 낮은 순)
     */
    List<Accommodation> findByTripBoardIdWithPagination(Long tripBoardId, int page, int size, Long userId, String sort);

    /**
     * 여행보드 ID로 숙소 개수를 조회
     * userId가 null이 아닌 경우 해당 사용자가 생성한 숙소 개수만 조회
     */
    Long countByTripBoardId(Long tripBoardId, Long userId);

    /**
     * 숙소를 저장합니다.
     */
    Accommodation save(AccommodationEntity accommodationEntity);

    /**
     * 숙소 ID로 조회, 실패시 에러를 던집니다.
     * 
     * @param accommodationId
     * @return
     */
    Accommodation findByIdOrThrow(Long accommodationId);

    void update(Accommodation updatedAccommodation);

    /**
     * 특정 여행보드에서 특정 사용자가 생성한 숙소들을 삭제합니다.
     */
    void deleteByTripBoardIdAndCreatedById(Long tripBoardId, Long createdById);

    /**
     * 여행보드 ID로 해당 보드의 모든 숙소를 삭제합니다.
     */
    void deleteByTripBoardId(Long tripBoardId);

    /**
     * 숙소 ID로 개별 숙소를 삭제합니다.
     */
    void deleteById(Long accommodationId);

}
