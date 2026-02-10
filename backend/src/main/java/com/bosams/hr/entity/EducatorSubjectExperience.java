package com.bosams.hr.entity;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name = "hr_educator_subject_experience", uniqueConstraints = @UniqueConstraint(name = "uq_hr_subject_experience", columnNames = {"school_id", "staff_id", "subject_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducatorSubjectExperience extends HrSchoolScopedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; @Column(name = "staff_id", nullable = false) private Long staffId; @Column(name = "subject_id", nullable = false) private Long subjectId;
    @Column(name = "years_experience", nullable = false) private Integer yearsExperience; @Column(columnDefinition = "text") private String notes;

}
