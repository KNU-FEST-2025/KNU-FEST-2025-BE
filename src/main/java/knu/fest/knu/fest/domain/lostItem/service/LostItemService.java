package knu.fest.knu.fest.domain.lostItem.service;


import knu.fest.knu.fest.domain.lostItem.dtos.request.CreateLostItmeRequest;
import org.springframework.web.multipart.MultipartFile;

public interface LostItemService {

    String create(Long userId, CreateLostItmeRequest request, MultipartFile image);
}
