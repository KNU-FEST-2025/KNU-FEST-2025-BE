package knu.fest.knu.fest.domain.lostItem.service;


import knu.fest.knu.fest.domain.lostItem.dtos.request.CreateLostItmeRequestDto;
import knu.fest.knu.fest.domain.lostItem.dtos.response.LostItemDto;
import knu.fest.knu.fest.domain.lostItem.dtos.response.ViewLostItemResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface LostItemService {

    String create(Long userId, CreateLostItmeRequestDto request);

    ViewLostItemResponseDto viewAll();

    LostItemDto getItem(Long id);

    String delete(Long id);

    String update(Long id, CreateLostItmeRequestDto request);
}
