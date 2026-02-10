package com.bosams.learnerparent.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity @Table(name="learner_mentor_assignments", uniqueConstraints=@UniqueConstraint(name="uq_mentor_assignment", columnNames={"school_id","learner_id","start_date"}))
public class LearnerMentorAssignment extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; private Long mentorStaffId; private LocalDate startDate; private LocalDate endDate; @Column(columnDefinition="text") private String notes;
public Long getId(){return id;} public Long getLearnerId(){return learnerId;} public void setLearnerId(Long v){learnerId=v;} public Long getMentorStaffId(){return mentorStaffId;} public void setMentorStaffId(Long v){mentorStaffId=v;} public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate v){startDate=v;} public LocalDate getEndDate(){return endDate;} public void setEndDate(LocalDate v){endDate=v;} public String getNotes(){return notes;} public void setNotes(String v){notes=v;}}
