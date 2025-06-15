package hgg.auth.adapter.out.persistence;

import hgg.auth.adapter.out.persistence.entity.UserEntity;
import hgg.domain.user.model.User;
import hgg.domain.user.port.out.CommandUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommandUserInfoRepository implements CommandUserPort {

    private final JpaUserRepository jpaUserRepository;

    @Override
    public User save(User user) {
        return jpaUserRepository.save(UserEntity.from(user)).toModel();
    }

    @Override
    public void deleteById(Long userId) {

    }
}
