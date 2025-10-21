package knu.fest.knu.fest.domain.comment.service;

import knu.fest.knu.fest.domain.booth.entity.Booth;
import knu.fest.knu.fest.domain.booth.repository.BoothRepository;
import knu.fest.knu.fest.domain.comment.controller.dto.CommentCreateRequest;
import knu.fest.knu.fest.domain.comment.controller.dto.CommentDeleteRequest;
import knu.fest.knu.fest.domain.comment.controller.dto.CommentResponse;
import knu.fest.knu.fest.domain.comment.entity.Comment;
import knu.fest.knu.fest.domain.comment.repository.CommentRepository;
import knu.fest.knu.fest.domain.user.entity.User;
import knu.fest.knu.fest.domain.user.entity.UserRole;
import knu.fest.knu.fest.domain.user.repository.UserRepository;
import knu.fest.knu.fest.global.exception.CommonException;
import knu.fest.knu.fest.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class CommentServiceImplTest {
    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BoothRepository boothRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private User admin;
    private Booth booth;

    @BeforeEach
    void setup() {
        user = User.builder()
                .nickname("user-test")
                .role(UserRole.USER)
                .email("tester@test.com")
                .password("user")
                .build();
        userRepository.save(user);

        admin = User.builder()
                .nickname("admin-test")
                .role(UserRole.ADMIN)
                .email("admin@test.com")
                .password("admin")
                .build();
        userRepository.save(admin);


        booth = Booth.builder()
                .name("Test Booth")
                .description("Test Description")
                .boothNumber(1)
                .likeCount(0L)
                .build();
        boothRepository.save(booth);
    }

    @Test
    void createComment_Success() {
        CommentCreateRequest request = new CommentCreateRequest(booth.getId(), "맛있어요!");

        CommentResponse response = commentService.create(user.getId(), request);

        assertThat(response.content()).isEqualTo("맛있어요!");
        assertThat(response.boothId()).isEqualTo(booth.getId());

        Comment savedComment = commentRepository.findById(response.id()).orElseThrow();
        assertThat(savedComment.getContent()).isEqualTo("맛있어요!");
    }

    @Test
    void deleteComment_Success() {
        Comment comment = Comment.builder()
                .booth(booth)
                .user(user)
                .content("삭제 테스트")
                .build();
        commentRepository.save(comment);

        CommentDeleteRequest request = new CommentDeleteRequest(comment.getId());

        commentService.delete(user.getId(), request);

        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }

    @Test
    void deleteComment_NoPermission_Throws() {
        User otherUser = User.builder()
                .nickname("other_user")
                .role(UserRole.USER)
                .email("other@test.com")
                .password("other")
                .build();
        userRepository.save(otherUser);

        Comment comment = Comment.builder()
                .booth(booth)
                .user(user)
                .content("삭제 불가 테스트")
                .build();
        commentRepository.save(comment);

        CommentDeleteRequest request = new CommentDeleteRequest(comment.getId());

        assertThatThrownBy(() -> commentService.delete(otherUser.getId(), request))
                .isInstanceOf(CommonException.class).hasMessageContaining(ErrorCode.COMMENT_EDIT_FORBIDDEN.getMessage());

        // 댓글이 그대로 남아있어야 함
        assertThat(commentRepository.findById(comment.getId())).isPresent();
    }

    @Test
    void deleteComment_Admin_Success() {
        Comment comment = Comment.builder()
                .booth(booth)
                .user(user)
                .content("관리자 삭제 테스트")
                .build();
        commentRepository.save(comment);

        CommentDeleteRequest request = new CommentDeleteRequest(comment.getId());

        commentService.delete(admin.getId(), request);

        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }
}