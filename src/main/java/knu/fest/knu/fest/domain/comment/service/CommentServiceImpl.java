package knu.fest.knu.fest.domain.comment.service;

import knu.fest.knu.fest.domain.booth.entity.Booth;
import knu.fest.knu.fest.domain.booth.repository.BoothRepository;
import knu.fest.knu.fest.domain.comment.controller.dto.CommentCreateRequest;
import knu.fest.knu.fest.domain.comment.controller.dto.CommentDeleteRequest;
import knu.fest.knu.fest.domain.comment.controller.dto.CommentResponse;
import knu.fest.knu.fest.domain.comment.entity.Comment;
import knu.fest.knu.fest.domain.comment.repository.CommentRepository;
import knu.fest.knu.fest.domain.user.entity.User;
import knu.fest.knu.fest.domain.user.repository.UserRepository;
import knu.fest.knu.fest.global.exception.CommonException;
import knu.fest.knu.fest.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BoothRepository boothRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentResponse create(Long userId, CommentCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Booth booth = boothRepository.findById(request.boothId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_BOOTH));

        Comment comment = Comment.builder()
                .booth(booth)
                .user(user)
                .content(request.content())
                .build();

        Comment saved = commentRepository.save(comment);
        return CommentResponse.from(saved);
    }

    @Override
    @Transactional
    public void delete(Long userId, CommentDeleteRequest request) {
        Comment comment = commentRepository.findById(request.commentId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_COMMENT));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        if (!comment.isOwner(userId) && !user.isAdmin()) {
            throw new CommonException(ErrorCode.COMMENT_EDIT_FORBIDDEN);
        }

        commentRepository.delete(comment);
    }

}
