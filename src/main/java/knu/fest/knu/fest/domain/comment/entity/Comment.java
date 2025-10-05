package knu.fest.knu.fest.domain.comment.entity;

import jakarta.persistence.*;
import knu.fest.knu.fest.domain.booth.entity.Booth;
import knu.fest.knu.fest.domain.user.entity.User;
import knu.fest.knu.fest.global.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booth_id")
    private Booth booth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Builder
    public Comment(Booth booth, User user, String content) {
        this.booth = booth;
        this.user = user;
        this.content = content;
    }
}
