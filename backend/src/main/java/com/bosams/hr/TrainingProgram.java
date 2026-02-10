package com.bosams.hr;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name = "hr_training_programs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingProgram extends HrSchoolScopedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; @Column(nullable = false) private String title; private String provider; @Column(name = "start_date", nullable = false) private LocalDate startDate; @Column(name = "end_date", nullable = false) private LocalDate endDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private TrainingCategory category; @Column(columnDefinition = "text") private String notes;

}
