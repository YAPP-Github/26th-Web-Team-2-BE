package hgg.domain.user.port.out;

import hgg.domain.user.model.User;
import java.util.Optional;

public interface QueryUserPort {
    Optional<User> getUser(String provider, String socialId);
    User getUserById(Long aLong);
}
