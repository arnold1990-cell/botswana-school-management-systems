package com.bosams.learnerparent.domain;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name="learner_applications", uniqueConstraints=@UniqueConstraint(name="uq_learner_application_no", columnNames={"school_id","application_no"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearnerApplication extends SchoolScopedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name="application_no",nullable=false) private String applicationNo;
    @Column(name="applied_date",nullable=false) private LocalDate appliedDate;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private ApplicationStatus status;
    private Long preferredGradeId; private Long preferredAcademicYearId;
    @Column(nullable=false) private String learnerFirstName; @Column(nullable=false) private String learnerLastName;
    private LocalDate dateOfBirth; private String gender; private String previousSchool;
    @Column(columnDefinition="text") private String notes; @Column(columnDefinition="text") private String decisionNote;
    private Instant decidedAt; private String decidedBy;

}
