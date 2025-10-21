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

    @Override
    @Transactional
    public NoticeDto getNotice(Long id) {
        Notice notice = noticeRepository.findById(id).orElseThrow(()->new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

        return toDto(notice);
    }

    @Override
    @Transactional
    public String delete(Long id) {
        try {
            if (noticeRepository.existsById(id)) {
                noticeRepository.deleteById(id);
                return "삭제 완료";
            }
            throw new IllegalArgumentException("해당 게시물이 존재하지 않습니다." + id);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    @Transactional
    public String update(Long id, CreateNoticeRequestDto requestDto) {

        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다. " + id));

        notice.updateNotice(requestDto.getTitle(),
                requestDto.getContent(),
                requestDto.getImagePath(),
                requestDto.getNoticeStatus());

        return "수정 완료";

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
