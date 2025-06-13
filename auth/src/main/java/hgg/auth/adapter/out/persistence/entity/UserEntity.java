package hgg.auth.adapter.out.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import hgg.domain.user.model.User;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(
                columnNames = { "provider", "social_id" }
        ))
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String provider;
    private String socialId;
    private String email;
    private String nickname;
    private String profileImage;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static UserEntity from(User user) {
        return UserEntity.builder()
                .id(null)
                .provider(user.getProvider())
                .socialId(user.getSocialId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public User toModel() {
        return User.builder()
                .id(id)
                .provider(provider)
                .socialId(socialId)
                .email(email)
                .nickname(nickname)
                .profileImage(profileImage)
                .build();
    }

}

