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

@Entity @Table(name="learner_leadership_roles", uniqueConstraints=@UniqueConstraint(name="uq_leadership", columnNames={"school_id","learner_id","role_type","academic_year_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearnerLeadershipRole extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; @Enumerated(EnumType.STRING) private LeadershipRoleType roleType; private Long academicYearId; private LocalDate startDate; private LocalDate endDate; private String notes;

}
