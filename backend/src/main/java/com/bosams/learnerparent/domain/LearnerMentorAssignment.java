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

@Entity @Table(name="learner_mentor_assignments", uniqueConstraints=@UniqueConstraint(name="uq_mentor_assignment", columnNames={"school_id","learner_id","start_date"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearnerMentorAssignment extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; private Long mentorStaffId; private LocalDate startDate; private LocalDate endDate; @Column(columnDefinition="text") private String notes;

}
