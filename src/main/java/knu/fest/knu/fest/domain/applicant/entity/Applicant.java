package knu.fest.knu.fest.domain.applicant.entity;

import jakarta.persistence.*;
import knu.fest.knu.fest.global.common.BaseEntity;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "applicants",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"student_num", "role"})
        }
)
public class Applicant extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long studentNum;

    @Column(nullable = false)
    private String department;

    @Enumerated(EnumType.STRING)
    private ApplicantRole role;

    public void update(String name, Long studentNum, String department, ApplicantRole role) {
        this.name = name;
        this.studentNum = studentNum;
        this.department = department;
        this.role = role;
    }

}
