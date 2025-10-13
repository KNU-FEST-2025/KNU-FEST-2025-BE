package knu.fest.knu.fest.domain.comment.service;

import knu.fest.knu.fest.domain.comment.controller.dto.CommentCreateRequest;
import knu.fest.knu.fest.domain.comment.controller.dto.CommentDeleteRequest;
import knu.fest.knu.fest.domain.comment.controller.dto.CommentResponse;

import java.util.List;

public interface CommentService {

    CommentResponse create(Long userId, CommentCreateRequest request);

    void delete(Long userId, CommentDeleteRequest request);

}
