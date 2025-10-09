package knu.fest.knu.fest.domain.like.service;

import knu.fest.knu.fest.domain.like.controller.dto.LikeRequest;
import knu.fest.knu.fest.domain.like.controller.dto.LikeResponse;


public interface LikeService {

    LikeResponse create(Long userId, LikeRequest request);
    void delete(Long userId, LikeRequest request);
}
