package com.yapp.backend.repository;

import com.yapp.backend.repository.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByProviderAndSocialId(String provider, String socialId);
}