package com.bosams.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "exam_group")
@Getter @Setter @NoArgsConstructor
public class ExamGroup {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "academic_year_id") private AcademicYear academicYear;
    @ManyToOne @JoinColumn(name = "term_id") private Term term;
    @Column(nullable = false) private String name;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Enums.ExamStatus status = Enums.ExamStatus.ACTIVE;
}
