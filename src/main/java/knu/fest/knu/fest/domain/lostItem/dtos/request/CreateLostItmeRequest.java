package knu.fest.knu.fest.domain.lostItem.dtos.request;

import knu.fest.knu.fest.domain.lostItem.entity.LostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateLostItmeRequest {

    private String content;
    private LostStatus lostStatus;
    private LocalDate lostAt;
}
