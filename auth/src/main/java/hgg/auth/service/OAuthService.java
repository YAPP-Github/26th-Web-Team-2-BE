package hgg.auth.service;

import hgg.auth.adapter.out.persistence.CommandUserInfoRepository;
import hgg.auth.adapter.out.persistence.QueryUserInfoRepository;
import hgg.domain.user.model.User;
import hgg.domain.user.port.in.UserLoginUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OAuthService implements UserLoginUseCase {

    private final CommandUserInfoRepository commandUserInfoRepository;
    private final QueryUserInfoRepository queryUserInfoRepository;

    /**
     * provider와 socialId로 user 정보를 조회하고,
     * 기존에 존재하지 않을 경우 새롭게 생성해 반환합니다.
     * @param userInfo
     * @return
     */
    @Override
    @Transactional
    public User handleOAuthLogin(User userInfo) {
        return queryUserInfoRepository.getUser(userInfo.getProvider(), userInfo.getSocialId())
                .orElseGet(() -> commandUserInfoRepository.save(userInfo));
    }
}
