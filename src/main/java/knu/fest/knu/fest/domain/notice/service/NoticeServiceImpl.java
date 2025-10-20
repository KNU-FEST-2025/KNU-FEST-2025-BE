package knu.fest.knu.fest.domain.notice.service;


import jakarta.transaction.Transactional;
import knu.fest.knu.fest.domain.notice.dtos.request.CreateNoticeRequestDto;
import knu.fest.knu.fest.domain.notice.dtos.response.NoticeDto;
import knu.fest.knu.fest.domain.notice.dtos.response.ViewNoticeResponseDto;
import knu.fest.knu.fest.domain.notice.entity.Notice;
import knu.fest.knu.fest.domain.notice.entity.NoticeStatus;
import knu.fest.knu.fest.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService{

    private final NoticeRepository noticeRepository;

    @Override
    @Transactional
    public String create(Long userId, CreateNoticeRequestDto request) {

        // 기본값 NORMAL
        NoticeStatus status = (request.getNoticeStatus() != null) ? request.getNoticeStatus() : NoticeStatus.NORMAL;

        Notice notice = Notice.builder()
                .user(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .imagePath(request.getImagePath())
                .noticeStatus(status)
                .build();

        Notice saved = noticeRepository.save(notice);
        return String.valueOf(saved.getId());
    }

    @Override
    @Transactional
    public ViewNoticeResponseDto viewAll() {
        List<Notice> items = noticeRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        List<NoticeDto> result = items.stream()
                .map(this::toDto)
                .toList();

        return new ViewNoticeResponseDto(result);
    }



    private NoticeDto toDto(Notice e) {
        return NoticeDto.builder()
                .id(e.getId())
                .title(e.getTitle())
                .content(e.getContent())
                .imagePath(e.getImagePath())
                .noticeStatus(e.getNoticeStatus())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getModifiedAt())
                .build();
    }
}
