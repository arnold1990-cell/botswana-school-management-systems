package com.bosams.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity @Table(name = "term")
@Getter @Setter @NoArgsConstructor
public class Term {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "academic_year_id") private AcademicYear academicYear;
    @Column(name="term_no", nullable = false) private int termNo;
    @Column(name="start_date", nullable = false) private LocalDate startDate;
    @Column(name="end_date", nullable = false) private LocalDate endDate;
}
