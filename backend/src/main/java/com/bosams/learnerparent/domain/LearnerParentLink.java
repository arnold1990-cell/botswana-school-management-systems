package com.bosams.learnerparent.domain;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name="learner_parent_links", uniqueConstraints=@UniqueConstraint(name="uq_learner_parent_link", columnNames={"school_id","learner_id","parent_id"}))
public class LearnerParentLink extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; private Long parentId; @Enumerated(EnumType.STRING) private RelationshipType relationshipType; private boolean isPrimaryContact; private boolean livesWithLearner;
public Long getId(){return id;} public Long getLearnerId(){return learnerId;} public void setLearnerId(Long v){learnerId=v;} public Long getParentId(){return parentId;} public void setParentId(Long v){parentId=v;} public RelationshipType getRelationshipType(){return relationshipType;} public void setRelationshipType(RelationshipType v){relationshipType=v;} public boolean isPrimaryContact(){return isPrimaryContact;} public void setPrimaryContact(boolean v){isPrimaryContact=v;} public boolean isLivesWithLearner(){return livesWithLearner;} public void setLivesWithLearner(boolean v){livesWithLearner=v;}
}
