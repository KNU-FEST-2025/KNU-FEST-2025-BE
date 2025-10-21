package knu.fest.knu.fest.domain.comment.repository;

import knu.fest.knu.fest.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByBoothId(Long boothId);
}
