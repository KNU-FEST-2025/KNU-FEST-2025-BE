package knu.fest.knu.fest.domain.lostItem.service;

import jakarta.transaction.Transactional;
import knu.fest.knu.fest.domain.lostItem.dtos.request.CreateLostItmeRequestDto;
import knu.fest.knu.fest.domain.lostItem.entity.LostItem;
import knu.fest.knu.fest.domain.lostItem.entity.LostStatus;
import knu.fest.knu.fest.domain.lostItem.repository.LostItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
                .build();

        LostItem saved = lostItemRepository.save(lostItem);
        return String.valueOf(saved.getId());
    }
}
