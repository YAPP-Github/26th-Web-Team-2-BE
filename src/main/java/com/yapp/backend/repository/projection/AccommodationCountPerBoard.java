package com.yapp.backend.repository.projection;

/**
 * 여행보드별 숙소 개수 조회를 위한 Projection 인터페이스
 */
public interface AccommodationCountPerBoard {

    /**
     * 여행보드 ID를 반환합니다.
     * 
     * @return 여행보드 ID
     */
    Long getTripBoardId();

    /**
     * 해당 여행보드의 숙소 개수를 반환합니다.
     * 
     * @return 숙소 개수
     */
    Long getCount();
}