package knu.fest.knu.fest.domain.notice.service;



import knu.fest.knu.fest.domain.notice.dtos.request.CreateNoticeRequestDto;
import knu.fest.knu.fest.domain.notice.dtos.response.NoticeDto;
import knu.fest.knu.fest.domain.notice.dtos.response.ViewNoticeResponseDto;
import knu.fest.knu.fest.domain.notice.entity.Notice;
import knu.fest.knu.fest.domain.notice.entity.NoticeStatus;
import knu.fest.knu.fest.domain.notice.repository.NoticeRepository;
import knu.fest.knu.fest.global.exception.CommonException;
import knu.fest.knu.fest.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeServiceImpl implements NoticeService{

    private final NoticeRepository noticeRepository;

    @Override
    public String create(Long userId, CreateNoticeRequestDto request) {

        // 기본값 NORMAL
        NoticeStatus status = (request.getNoticeStatus() != null) ? request.getNoticeStatus() : NoticeStatus.NORMAL;

        Notice notice = Notice.builder()
                .user(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .noticeStatus(status)
                .imagePath(request.getImagePath())
                .build();

//        request.getImagePath().stream()
//                .map(p -> imageRepository.findByImage(p)
//                        .orElseThrow(() -> new IllegalArgumentException("이미지 경로를 찾을 수 없습니다: " + p))
//                        .getId())

        Notice saved = noticeRepository.save(notice);
        return String.valueOf(saved.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public ViewNoticeResponseDto viewAll() {
        List<Notice> items = noticeRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        List<NoticeDto> result = items.stream()
                .map(this::toDto)
                .toList();

        return new ViewNoticeResponseDto(result);
    }

    @Override
    @Transactional(readOnly = true)
    public NoticeDto getNotice(Long id) {
        Notice notice = noticeRepository.findById(id).orElseThrow(()->new CommonException(ErrorCode.NOT_FOUND_NOTICE));

        return toDto(notice);
    }

    @Override
    public String delete(Long id) {
        try {
            if (noticeRepository.existsById(id)) {
                noticeRepository.deleteById(id);
                return "삭제 완료";
            }
            throw new CommonException(ErrorCode.NOT_FOUND_NOTICE);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public String update(Long id, CreateNoticeRequestDto requestDto) {

        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_NOTICE));

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
                .imagePath(e.getImagePath().toString())
                .noticeStatus(e.getNoticeStatus())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getModifiedAt())
                .build();
    }
}
