package hgg.auth.dto;

import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;


/**
 * CustomOAuth2User는 DefaultOAuth2User를 확장하여
 * OAuth2 로그인 시 획득한 사용자 속성을 보관하는 객체입니다.
 * (필요하다면 추가 필드를 더하여 생성합니다)
 */
@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    /**
     * 새로운 CustomOAuth2User 인스턴스를 생성합니다.
     *
     * @param authorities      사용자가 가진 권한 목록(GrantedAuthority)
     * @param attributes       OAuth2 제공자로부터 전달받은 사용자 속성 맵
     * @param nameAttributeKey 사용자 고유 식별자(ID)를 attributes 맵에서 조회할 때 사용할 키 이름
     */
    public CustomOAuth2User(
            Collection<? extends GrantedAuthority> authorities,
            Map<String, Object> attributes,
            String nameAttributeKey
    ) {
        super(authorities, attributes, nameAttributeKey);
    }
}
