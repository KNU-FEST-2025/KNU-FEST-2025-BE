package knu.fest.knu.fest.domain.notice.entity;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "notice")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 게시글 등록 유저
    @Column(name = "user_id", nullable = false)
    private Long user;

    // 게시글 제목
    @Column(name = "titel", nullable = false)
    private String title;

    // 게시물 내용
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 사진 파일 경로 (또는 URL)
    private String imagePath;

    // 분실물 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoticeStatus noticeStatus;

    @Builder
    public Notice(Long user,
                    String title,
                    String content,
                    String imagePath,
                    NoticeStatus noticeStatus) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
        this.noticeStatus = noticeStatus;
    }
}
