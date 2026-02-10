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

@Entity @Table(name="learner_parent_links", uniqueConstraints=@UniqueConstraint(name="uq_learner_parent_link", columnNames={"school_id","learner_id","parent_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearnerParentLink extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; private Long parentId; @Enumerated(EnumType.STRING) private RelationshipType relationshipType; private boolean isPrimaryContact; private boolean livesWithLearner;

}
