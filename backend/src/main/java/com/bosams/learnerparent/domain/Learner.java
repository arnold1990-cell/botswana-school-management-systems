package com.bosams.learnerparent.domain;

import com.bosams.learnerparent.domain.enums.*;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name="learners", uniqueConstraints=@UniqueConstraint(name="uq_learner_no", columnNames={"school_id","learner_no"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Learner extends SchoolScopedEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="learner_no",nullable=false) private String learnerNo;
    @Column(nullable=false) private String firstName; @Column(nullable=false) private String lastName;
    private LocalDate dateOfBirth; private String gender; private String nationalId; private String homeLanguage;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private LearnerStatus status;
    private Long currentAcademicYearId; private Long currentGradeId; private Long currentClassRoomId; private Long houseId;
    private LocalDate admissionDate; private Instant archivedAt; private String archiveReason;

}
