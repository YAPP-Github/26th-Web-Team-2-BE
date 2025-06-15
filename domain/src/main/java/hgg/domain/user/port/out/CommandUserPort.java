package hgg.domain.user.port.out;

import hgg.domain.user.model.User;

public interface CommandUserPort {
    User save(User user);
    void deleteById(Long userId);
}
