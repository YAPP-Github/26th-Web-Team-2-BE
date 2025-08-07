package com.yapp.backend.repository.impl;

import com.yapp.backend.repository.JpaUserTripBoardRepository;
import com.yapp.backend.repository.UserTripBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 사용자-여행보드 매핑 Repository 구현체
 */
@Repository
@RequiredArgsConstructor
public class UserTripBoardRepositoryImpl implements UserTripBoardRepository {

    private final JpaUserTripBoardRepository jpaUserTripBoardRepository;

    @Override
    public void deleteByTripBoardId(Long tripBoardId) {
        jpaUserTripBoardRepository.deleteByTripBoardId(tripBoardId);
    }
}