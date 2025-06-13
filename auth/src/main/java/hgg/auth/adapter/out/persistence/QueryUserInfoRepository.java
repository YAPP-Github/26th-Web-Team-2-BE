package hgg.auth.adapter.out.persistence;

import hgg.auth.adapter.out.persistence.entity.UserEntity;
import hgg.domain.user.model.User;
import hgg.domain.user.port.out.QueryUserPort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryUserInfoRepository implements QueryUserPort {

    private final JpaUserRepository jpaUserRepository;

    @Override
    public Optional<User> getUser(String provider, String socialId) {
        return jpaUserRepository.findByProviderAndSocialId(provider, socialId)
                .map(UserEntity::toModel);
    }
}
