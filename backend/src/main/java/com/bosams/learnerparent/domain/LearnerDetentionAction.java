package com.bosams.learnerparent.domain;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name="learner_detention_actions")
public class LearnerDetentionAction extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; private LocalDate scheduledDate; private Integer durationMinutes; private String reason; @Enumerated(EnumType.STRING) private DetentionStatus status;
public Long getId(){return id;} public Long getLearnerId(){return learnerId;} public void setLearnerId(Long v){learnerId=v;} public LocalDate getScheduledDate(){return scheduledDate;} public void setScheduledDate(LocalDate v){scheduledDate=v;} public Integer getDurationMinutes(){return durationMinutes;} public void setDurationMinutes(Integer v){durationMinutes=v;} public String getReason(){return reason;} public void setReason(String v){reason=v;} public DetentionStatus getStatus(){return status;} public void setStatus(DetentionStatus v){status=v;}}
