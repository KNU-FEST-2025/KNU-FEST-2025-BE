package knu.fest.knu.fest.domain.user.repository;


import knu.fest.knu.fest.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query(
          "select u.nickname from User u where u.id = :id"
    )
    String findNicknameByUserId(@Param("id") Long userId);
}
