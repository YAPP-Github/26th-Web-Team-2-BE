package hgg.domain.user.port.in;

import hgg.domain.user.model.User;

public interface UserLoginUseCase {
    User handleOAuthLogin(User user);
}
