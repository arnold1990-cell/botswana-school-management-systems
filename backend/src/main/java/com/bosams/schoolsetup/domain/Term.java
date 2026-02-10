package com.bosams.schoolsetup.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name="LegacyTerm")
@Table(name = "terms", uniqueConstraints = @UniqueConstraint(columnNames = {"academic_year_id","name"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Term extends SchoolOwnedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "academic_year_id") private AcademicYear academicYear;
    @Column(nullable = false, length = 100) private String name;
    private LocalDate startDate;
    private LocalDate endDate;

}
