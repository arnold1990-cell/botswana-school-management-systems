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

@Entity @Table(name="learner_activity_memberships", uniqueConstraints=@UniqueConstraint(name="uq_learner_activity", columnNames={"school_id","learner_id","activity_type","activity_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearnerActivityMembership extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; @Enumerated(EnumType.STRING) private ActivityType activityType; private Long activityId; @Enumerated(EnumType.STRING) private ActivityRole role; private LocalDate startDate; private LocalDate endDate;

}
