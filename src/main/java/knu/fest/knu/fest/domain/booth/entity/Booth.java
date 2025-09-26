package knu.fest.knu.fest.domain.booth.entity;


import jakarta.persistence.*;
import knu.fest.knu.fest.global.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "booth")
public class Booth extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=120)
    private String name;

    @Column(columnDefinition="TEXT")
    private String description;

    @Column(name = "booth_number", unique = true, nullable=false)
    private Integer boothNumber;

    @Column(nullable=false)
    private Long waitingCount = 0L;

    @Column(nullable = false)
    private Long likeCount = 0L;

    @Builder
    public Booth(String name, String description, Integer boothNumber) {
        this.name = name;
        this.description = description;
        this.boothNumber = boothNumber;
    }
}
