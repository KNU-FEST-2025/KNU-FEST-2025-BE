package knu.fest.knu.fest.domain.Like.service;

import knu.fest.knu.fest.domain.Like.controller.dto.LikeRequest;
import knu.fest.knu.fest.domain.Like.controller.dto.LikeResponse;
import knu.fest.knu.fest.domain.Like.entity.Like;
import knu.fest.knu.fest.domain.Like.repository.LikeRepository;
import knu.fest.knu.fest.domain.booth.entity.Booth;
import knu.fest.knu.fest.domain.booth.repository.BoothRepository;
import knu.fest.knu.fest.domain.user.entity.User;
import knu.fest.knu.fest.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


public interface LikeService {

    LikeResponse create(LikeRequest request);
    void delete(LikeRequest request);
}
