package knu.fest.knu.fest.domain.notice.dtos.response;

import knu.fest.knu.fest.domain.notice.entity.NoticeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeDto {
    private Long id;
    private String title;
    private String content;
    private String imagePath;
    private NoticeStatus noticeStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
