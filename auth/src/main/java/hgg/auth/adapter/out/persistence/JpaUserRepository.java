package hgg.auth.adapter.out.persistence;

import hgg.auth.adapter.out.persistence.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByProviderAndSocialId(String provider, String socialId);
}
