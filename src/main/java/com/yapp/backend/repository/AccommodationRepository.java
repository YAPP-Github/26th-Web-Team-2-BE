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
    List<Accommodation> findByBoardIdWithPagination(Long boardId, int page, int size, Long userId, String sort);

    /**
     * 여행보드 ID로 숙소 개수를 조회
     * userId가 null이 아닌 경우 해당 사용자가 생성한 숙소 개수만 조회
     */
    Long countByBoardId(Long boardId, Long userId);

    /**
     * 숙소를 저장합니다.
     */
    Accommodation save(AccommodationEntity accommodationEntity);

    /**
     * 숙소 ID로 단건 조회합니다.
     */
    Accommodation findById(Long accommodationId);

    /**
     * 숙소 ID로 조회, 실패시 에러를 던집니다.
     * @param accommodationId
     * @return
     */
    Accommodation findByIdOrThrow(Long accommodationId);

    void update(Accommodation updatedAccommodation);

}
