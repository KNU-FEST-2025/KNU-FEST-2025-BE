package knu.fest.knu.fest.domain.notice.service;

import knu.fest.knu.fest.domain.notice.dtos.request.CreateNoticeRequestDto;
import knu.fest.knu.fest.domain.notice.dtos.response.NoticeDto;
import knu.fest.knu.fest.domain.notice.dtos.response.ViewNoticeResponseDto;

public interface NoticeService {

    String create(Long userId, CreateNoticeRequestDto request);

    ViewNoticeResponseDto viewAll();

    NoticeDto getNotice(Long id);

    String delete(Long id);

    String update(Long id, CreateNoticeRequestDto requestDto);
}
