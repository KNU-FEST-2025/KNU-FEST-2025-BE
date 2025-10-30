package knu.fest.knu.fest.domain.booth.service;

import knu.fest.knu.fest.domain.booth.controller.dto.*;
import knu.fest.knu.fest.domain.booth.entity.Booth;
import knu.fest.knu.fest.domain.booth.repository.BoothManagerRepository;
import knu.fest.knu.fest.domain.booth.repository.BoothRepository;
import knu.fest.knu.fest.domain.comment.controller.dto.CommentResponse;
import knu.fest.knu.fest.domain.comment.repository.CommentRepository;
import knu.fest.knu.fest.domain.like.repository.LikeRepository;
import knu.fest.knu.fest.global.exception.CommonException;
import knu.fest.knu.fest.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoothService {

    private final BoothRepository boothRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final BoothManagerRepository boothManagerRepository;

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
                .imagePath(request.imagePath())
                .longitude(request.longitude())
                .latitude(request.latitude())
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
        // 지연 컬렉션 미리 터치해서 초기화
        List<String> imagePaths = new ArrayList<>(booth.getImagePath()); // size() 호출 등도 OK

        return BoothDetailResponse.of(booth, imagePaths, comments, likedByMe);
    }

    @Transactional(readOnly = true)
    public BoothDetailResponse getBooth(Long id, Long userId) {
        Booth booth = boothRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_BOOTH));

        // 지연 컬렉션 미리 터치해서 초기화
        List<String> imagePaths = new ArrayList<>(booth.getImagePath()); // size() 호출 등도 OK

        List<CommentResponse> comments = commentRepository.findAllByBoothId(id)
                .stream()
                .map(CommentResponse::from)
                .toList();

        boolean likedByMe = false;
        if (userId != null) {
            likedByMe = likeRepository.existsByUserIdAndBoothId(userId, id);
        }

        return BoothDetailResponse.of(booth,imagePaths, comments, likedByMe);
    }

    @Transactional(readOnly = true)
    public List<BoothListResponse> getAllBooths(Long userId) {
        List<Booth> booths = boothRepository.findAll();

        Set<Long> likedBoothIds = new HashSet<>();
        if (userId != null) {
            likedBoothIds.addAll(likeRepository.findAllBoothIdsByUserId(userId));
        }

        return booths.stream()
                .map(booth -> BoothListResponse.of(
                        booth,
                        likedBoothIds.contains(booth.getId())
                ))
                .sorted(
                        Comparator
                                .comparing(BoothListResponse::likedByMe, Comparator.reverseOrder())
                                .thenComparing(booth -> booth.likeCount() + booth.waitingCount() + booth.commentCount(),
                                        Comparator.reverseOrder())
                )
                .collect(Collectors.toList());
    }



    @Transactional(readOnly = true)
    public BoothManagerResponse getBoothManagedByUser(Long userId) {
        Booth booth = boothManagerRepository.findBoothByUserId(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_BOOTH));

        return new BoothManagerResponse(booth.getId(), booth.getName());
    }

}
