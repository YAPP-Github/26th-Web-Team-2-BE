package com.yapp.backend.repository;

import com.yapp.backend.repository.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * 삭제 여부를 신경쓰지 않고 모든 사용자를 조회
     * @param provider
     * @param socialId
     * @return
     */
    Optional<UserEntity> findByProviderAndSocialId(String provider, String socialId);

    /**
     * 삭제되지 않은 사용자만 조회 (소프트 삭제 고려)
     */
    Optional<UserEntity> findByProviderAndSocialIdAndDeletedAtIsNull(String provider, String socialId);
}