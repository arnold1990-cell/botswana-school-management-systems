package com.bosams.learnerparent.domain;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name="learner_activity_memberships", uniqueConstraints=@UniqueConstraint(name="uq_learner_activity", columnNames={"school_id","learner_id","activity_type","activity_id"}))
public class LearnerActivityMembership extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; @Enumerated(EnumType.STRING) private ActivityType activityType; private Long activityId; @Enumerated(EnumType.STRING) private ActivityRole role; private LocalDate startDate; private LocalDate endDate;
public Long getId(){return id;} public Long getLearnerId(){return learnerId;} public void setLearnerId(Long v){learnerId=v;} public ActivityType getActivityType(){return activityType;} public void setActivityType(ActivityType v){activityType=v;} public Long getActivityId(){return activityId;} public void setActivityId(Long v){activityId=v;} public ActivityRole getRole(){return role;} public void setRole(ActivityRole v){role=v;} public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate v){startDate=v;} public LocalDate getEndDate(){return endDate;} public void setEndDate(LocalDate v){endDate=v;}}
