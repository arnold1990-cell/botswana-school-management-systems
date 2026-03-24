package com.bosams.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subject")
@Getter
@Setter
@NoArgsConstructor
public class SubjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 100)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "school_level", nullable = false)
    private Enums.SchoolLevel schoolLevel = Enums.SchoolLevel.PRIMARY;

    @Column(name = "grade_from", nullable = false)
    private Integer gradeFrom = 1;

    @Column(name = "grade_to", nullable = false)
    private Integer gradeTo = 7;

    @Column(name = "is_elective", nullable = false)
    private boolean elective = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Enums.EntityStatus status = Enums.EntityStatus.ACTIVE;
}
