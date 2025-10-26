package knu.fest.knu.fest.domain.lostItem.dtos.response;


import knu.fest.knu.fest.domain.lostItem.entity.LostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LostItemDto {
    private Long id;
    private String title;
    private String content;
    private LocalDate foundTime;
    private String Location;
    private String imagePath;
    private LostStatus lostStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
