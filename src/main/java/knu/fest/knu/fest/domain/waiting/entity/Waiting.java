package knu.fest.knu.fest.domain.waiting.entity;

import jakarta.persistence.*;
import knu.fest.knu.fest.domain.booth.entity.Booth;
import knu.fest.knu.fest.global.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "waiting")
public class Waiting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=10)
    private String nickName;

    @Column(nullable=false, length=15)
    private String phone;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime reserveTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WaitingStatus status = WaitingStatus.WAITING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booth_id")
    private Booth booth;

    public void assignBooth(Booth booth) {
        this.booth = booth;
    }

}
