package com.bosams.learnerparent.domain;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name="learner_discipline_entries")
public class LearnerDisciplineEntry extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; private LocalDate entryDate; @Enumerated(EnumType.STRING) private DisciplineEntryType entryType; private Long codeId; private Integer points; private String notes; private String capturedBy;
public Long getId(){return id;} public Long getLearnerId(){return learnerId;} public void setLearnerId(Long v){learnerId=v;} public LocalDate getEntryDate(){return entryDate;} public void setEntryDate(LocalDate v){entryDate=v;} public DisciplineEntryType getEntryType(){return entryType;} public void setEntryType(DisciplineEntryType v){entryType=v;} public Long getCodeId(){return codeId;} public void setCodeId(Long v){codeId=v;} public Integer getPoints(){return points;} public void setPoints(Integer v){points=v;} public String getNotes(){return notes;} public void setNotes(String v){notes=v;} public String getCapturedBy(){return capturedBy;} public void setCapturedBy(String v){capturedBy=v;}}
