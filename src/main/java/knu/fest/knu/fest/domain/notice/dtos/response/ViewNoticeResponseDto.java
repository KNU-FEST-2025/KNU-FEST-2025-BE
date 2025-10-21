package knu.fest.knu.fest.domain.notice.dtos.response;

import knu.fest.knu.fest.domain.notice.entity.Notice;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ViewNoticeResponseDto {
    private List<NoticeDto> items;
}
