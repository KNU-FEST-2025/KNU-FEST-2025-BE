package knu.fest.knu.fest.domain.booth.entity;


import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name="booth_images")
public class BoothImage { //나중에 파일 클래스 extends
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="booth_id", nullable=false)
    private Booth booth;

    @Column(nullable=false)
    private String url;
}
