package knu.fest.knu.fest.domain.notice.service;

import knu.fest.knu.fest.domain.notice.dtos.request.CreateNoticeRequestDto;

public interface NoticeService {

    String create(Long userId, CreateNoticeRequestDto request);
}
