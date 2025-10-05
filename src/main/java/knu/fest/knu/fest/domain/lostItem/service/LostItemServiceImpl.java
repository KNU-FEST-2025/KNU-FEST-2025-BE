package knu.fest.knu.fest.domain.lostItem.service;

import jakarta.transaction.Transactional;
import knu.fest.knu.fest.domain.lostItem.dtos.request.CreateLostItmeRequestDto;
import knu.fest.knu.fest.domain.lostItem.dtos.response.LostItemDto;
import knu.fest.knu.fest.domain.lostItem.dtos.response.ViewLostItemResponseDto;
import knu.fest.knu.fest.domain.lostItem.entity.LostItem;
import knu.fest.knu.fest.domain.lostItem.entity.LostStatus;
import knu.fest.knu.fest.domain.lostItem.repository.LostItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LostItemServiceImpl implements LostItemService{

    private final LostItemRepository lostItemRepository;

    @Override
    @Transactional
    public String create(Long userId, CreateLostItmeRequestDto request){

        // 상태 기본값 필요 시 LOST로 디폴트
        LostStatus status = (request.getLostStatus() != null) ? request.getLostStatus() : LostStatus.LOST;

        LostItem lostItem = LostItem.builder()
                .user(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .imagePath(request.getImagePath())
                .foundTime(request.getFoundTime())
                .lostStatus(status)
                .location(request.getLocation())
                .build();

        LostItem saved = lostItemRepository.save(lostItem);
        return String.valueOf(saved.getId());
    }

    @Override
    @Transactional
    public ViewLostItemResponseDto viewAll() {
        List<LostItem> items = lostItemRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        List<LostItemDto> result = items.stream()
                .map(this::toDto)
                .toList();

        return new ViewLostItemResponseDto(result);
    }

    @Override
    @Transactional
    public LostItemDto getItem(Long id) {
        LostItem item = lostItemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다.: " + id));

        return toDto(item);
    }

    @Override
    @Transactional
    public String delete(Long id) {
        try{
            if (lostItemRepository.existsById(id)) {
                lostItemRepository.deleteById(id);
                return "삭제 완료";
            }
            throw new IllegalArgumentException("해당 게시물이 존재하지 않습니다." + id);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    @Transactional
    public String update(Long id, CreateLostItmeRequestDto request) {
        LostItem item = lostItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 없습니다. id=" + id));

        item.updateItem(request.getTitle(),
                        request.getContent(),
                        request.getImagePath(),
                        request.getFoundTime(),
                        request.getLostStatus(),
                        request.getLocation()
                        );

        return "수정을 완료했습니다.";
    }




    private LostItemDto toDto(LostItem e) {
        return LostItemDto.builder()
                .id(e.getId())
                .userId(e.getUser())
                .title(e.getTitle())
                .content(e.getContent())
                .foundTime(e.getFoundTime())       // LocalDateTime 가정
                .lostStatus(e.getLostStatus())     // enum
                .createdAt(e.getCreatedAt())       // @CreationTimestamp 가정
                .updatedAt(e.getModifiedAt())       // @UpdateTimestamp 가정
                .build();
    }
}
