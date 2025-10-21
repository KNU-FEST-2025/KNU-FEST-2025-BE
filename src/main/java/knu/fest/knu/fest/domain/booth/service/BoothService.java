package knu.fest.knu.fest.domain.booth.service;

import knu.fest.knu.fest.domain.booth.controller.dto.BoothCreateRequest;
import knu.fest.knu.fest.domain.booth.controller.dto.BoothCreateResponse;
import knu.fest.knu.fest.domain.booth.controller.dto.BoothDetailResponse;
import knu.fest.knu.fest.domain.booth.controller.dto.BoothUpdateRequest;
import knu.fest.knu.fest.domain.booth.entity.Booth;
import knu.fest.knu.fest.domain.booth.repository.BoothRepository;
import knu.fest.knu.fest.domain.comment.controller.dto.CommentResponse;
import knu.fest.knu.fest.domain.comment.repository.CommentRepository;
import knu.fest.knu.fest.global.exception.CommonException;
import knu.fest.knu.fest.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoothService {

    private final BoothRepository boothRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public BoothCreateResponse createBooth(BoothCreateRequest request) {
        if (boothRepository.existsByBoothNumber(request.boothNumber())) {
            throw new CommonException(ErrorCode.ALREADY_EXIST_BOOTH_NUMBER);
        }
        Booth booth = request.toEntity();
        Booth saved = boothRepository.save(booth);

        return BoothCreateResponse.from(saved);
    }

    @Transactional
    public BoothDetailResponse updateBooth(Long id, BoothUpdateRequest request) {
        Booth booth = boothRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_BOOTH));

        booth.update(request.name(), request.description());
        List<CommentResponse> comments = commentRepository.findAllByBoothId(id)
                .stream()
                .map(CommentResponse::from)
                .toList();

        return BoothDetailResponse.of(booth, comments);
    }

    @Transactional(readOnly = true)
    public BoothDetailResponse getBooth(Long id) {
        Booth booth = boothRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_BOOTH));

        List<CommentResponse> comments = commentRepository.findAllByBoothId(id)
                .stream()
                .map(CommentResponse::from)
                .toList();

        return BoothDetailResponse.of(booth, comments);
    }

/**
 * 목록 조회 API는 웨이팅 기능 개발 이후에 구현 예정
 *
    @Transactional(readOnly = true)
    public List<BoothDetailResponse> list() {
        List<Booth> booths = boothRepository.findAll();
        return booths.stream().map(BoothDetailResponse::of).toList();
    }
    */
}
