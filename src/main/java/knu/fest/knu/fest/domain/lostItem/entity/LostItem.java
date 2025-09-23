package knu.fest.knu.fest.domain.lostItem.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    // 게시물 내용
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 어디 주막에서 잃어버렸는지
//    @NotNull
//    @Column(nullable = false)
//    private String location;

    // 사진 파일 경로 (또는 URL)
    private String imagePath;

    @Column(name = "lost_at", columnDefinition = "DATE")
    private LocalDate lostAt;

    @Builder
    public LostItem(String content, String imagePath, LocalDate lostAt) {
        this.content = content;
        this.imagePath = imagePath;
        this.lostAt = lostAt;
    }
}
