package knu.fest.knu.fest.domain.lostItem.dtos.response;


import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
public class ViewLostItemResponseDto {
    private List<LostItemDto> items;
}
