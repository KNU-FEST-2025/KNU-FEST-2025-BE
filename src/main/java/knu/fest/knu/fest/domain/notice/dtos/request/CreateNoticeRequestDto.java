package knu.fest.knu.fest.domain.notice.dtos.request;

import knu.fest.knu.fest.domain.notice.entity.NoticeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateNoticeRequestDto {

    private String title;
    private String content;
    private NoticeStatus noticeStatus;
    private String imagePath;
}
