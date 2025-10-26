package knu.fest.knu.fest.domain.booth.service;

import knu.fest.knu.fest.domain.booth.controller.dto.*;
import knu.fest.knu.fest.domain.booth.entity.Booth;
import knu.fest.knu.fest.domain.booth.repository.BoothRepository;
import knu.fest.knu.fest.domain.comment.controller.dto.CommentResponse;
import knu.fest.knu.fest.domain.comment.repository.CommentRepository;
import knu.fest.knu.fest.domain.like.repository.LikeRepository;
import knu.fest.knu.fest.global.exception.CommonException;
import knu.fest.knu.fest.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoothService {

    private final BoothRepository boothRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    @Transactional
    public BoothCreateResponse createBooth(BoothCreateRequest request) {
        if (boothRepository.existsByBoothNumber(request.boothNumber())) {
            throw new CommonException(ErrorCode.ALREADY_EXIST_BOOTH_NUMBER);
        }
        Booth booth = Booth.builder()
                .name(request.name())
                .description(request.description())
                .boothNumber(request.boothNumber())
                .likeCount(0L)
                .waitingCount(0L)
                .build();
        Booth saved = boothRepository.save(booth);

        return BoothCreateResponse.from(saved);
    }

    @Transactional
    public BoothDetailResponse updateBooth(Long id, BoothUpdateRequest request, Long userId) {
        Booth booth = boothRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_BOOTH));

        booth.update(request.name(), request.description());
        List<CommentResponse> comments = commentRepository.findAllByBoothId(id)
                .stream()
                .map(CommentResponse::from)
                .toList();

        boolean likedByMe = false;
        if (userId != null) {
            likedByMe = likeRepository.existsByUserIdAndBoothId(userId, id);
        }

        return BoothDetailResponse.of(booth, comments, likedByMe);
    }

    @Transactional(readOnly = true)
    public BoothDetailResponse getBooth(Long id, Long userId) {
        Booth booth = boothRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_BOOTH));

        List<CommentResponse> comments = commentRepository.findAllByBoothId(id)
                .stream()
                .map(CommentResponse::from)
                .toList();

        boolean likedByMe = false;
        if (userId != null) {
            likedByMe = likeRepository.existsByUserIdAndBoothId(userId, id);
        }

        return BoothDetailResponse.of(booth, comments, likedByMe);
    }

    @Transactional(readOnly = true)
    public List<BoothListResponse> getAllBooths(Long userId) {
        List<Booth> booths = boothRepository.findAll();

        return booths.stream()
                .sorted(Comparator.comparingLong(Booth::getWaitingCount).reversed())
                .map(booth -> {
                    boolean likedByMe = false;
                    if (userId != null) {
                        likedByMe = likeRepository.existsByUserIdAndBoothId(userId, booth.getId());
                    }
                    return BoothListResponse.of(booth, likedByMe);
                })
                .collect(Collectors.toList());
    }

}
