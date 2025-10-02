package knu.fest.knu.fest.domain.lostItem.entity;


import jakarta.persistence.*;
import knu.fest.knu.fest.global.common.BaseEntity;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "lostItem")
public class LostItem extends BaseEntity {

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

    // 어디 주막에서 잃어버렸는지
    @Column(nullable = false)
    private String location;

    // 사진 파일 경로 (또는 URL)
    private String imagePath;

    // 분실물 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LostStatus lostStatus;

    @Column(name = "lost_at", columnDefinition = "DATE")
    private LocalDate foundTime;

    @Builder
    public LostItem(Long user,
                    String title,
                    String content,
                    String imagePath,
                    LocalDate foundTime,
                    LostStatus lostStatus) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
        this.foundTime = foundTime;
        this.lostStatus = lostStatus;
    }
}
