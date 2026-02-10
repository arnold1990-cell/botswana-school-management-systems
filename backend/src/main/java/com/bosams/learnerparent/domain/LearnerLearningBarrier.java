package com.bosams.learnerparent.domain;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name="learner_learning_barriers")
public class LearnerLearningBarrier extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; @Enumerated(EnumType.STRING) private BarrierType barrierType; @Column(columnDefinition="text") private String notes; private LocalDate identifiedDate;
public Long getId(){return id;} public Long getLearnerId(){return learnerId;} public void setLearnerId(Long v){learnerId=v;} public BarrierType getBarrierType(){return barrierType;} public void setBarrierType(BarrierType v){barrierType=v;} public String getNotes(){return notes;} public void setNotes(String v){notes=v;} public LocalDate getIdentifiedDate(){return identifiedDate;} public void setIdentifiedDate(LocalDate v){identifiedDate=v;}}
