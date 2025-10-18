package knu.fest.knu.fest.domain.lostItem.repository;

import knu.fest.knu.fest.domain.lostItem.entity.LostItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LostItemRepository extends JpaRepository<LostItem, Long> {
}
