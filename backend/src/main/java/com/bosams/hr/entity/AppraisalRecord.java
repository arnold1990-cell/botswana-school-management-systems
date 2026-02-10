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

@Entity @Table(name = "hr_appraisal_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppraisalRecord extends HrSchoolScopedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; @Column(name = "staff_id", nullable = false) private Long staffId; @Column(name = "appraisal_date", nullable = false) private LocalDate appraisalDate;
    @Enumerated(EnumType.STRING) @Column(name = "appraisal_type", nullable = false) private AppraisalType appraisalType; @Column(name = "reviewer_name", nullable = false) private String reviewerName;
    private Integer score; @Column(columnDefinition = "text") private String summary; @Column(columnDefinition = "text") private String recommendations;

}
