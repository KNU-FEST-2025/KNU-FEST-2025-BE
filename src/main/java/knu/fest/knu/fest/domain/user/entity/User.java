package knu.fest.knu.fest.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import knu.fest.knu.fest.global.common.BaseEntity;
import lombok.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String email;

    @NotNull
    @Column(nullable = false)
    private String password;

    @Size(min = 2, max = 10)
    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = true)
    private String profileImageUrl;

    @Column(nullable = true)
    private Provider provider;

    @Builder
    public User(String email, String password, String nickname, UserRole role, String profileImageUrl, Provider provider) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.profileImageUrl = profileImageUrl;
        this.provider = provider;
    }
}
