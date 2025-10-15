package knu.fest.knu.fest.domain.booth.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import knu.fest.knu.fest.domain.waiting.entity.Waiting;
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
@Table(name = "boothes")
public class Booth extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=120)
    private String name;

    @Column(columnDefinition="TEXT")
    private String description;

    @Column(name = "booth_number", unique = true, nullable=false)
    private Integer boothNumber;

    @Column(name = "waiting_count", nullable=false)
    private Long waitingCount = 0L;

    @Column(name = "like_count", nullable = false)
    private Long likeCount = 0L;
/**
    @OneToMany(mappedBy = "booth", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Waiting> waitings = new ArrayList<>();

    public void addWaiting(Waiting waiting) {
        if (!waitings.contains(waiting)) {
            waitings.add(waiting);
            waiting.assignBooth(this);
        }
    }

    public void removeWaiting(Waiting waiting) {
        waitings.remove(waiting);
        waiting.assignBooth(null);
    }
*/

    public void addLike() {
        this.likeCount += 1;
    }

    public void removeLike() {
        if (this.likeCount > 0) {
            this.likeCount -= 1;
        }
    }

    @Builder
    public Booth(String name, String description, Integer boothNumber, Long likeCount) {
        this.name = name;
        this.description = description;
        this.boothNumber = boothNumber;
        this.likeCount = likeCount;
    }

    public void update(@NotBlank @Size(max = 120) String name, String description) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
    }
}
