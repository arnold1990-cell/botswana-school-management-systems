package com.bosams.learnerparent.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity @Table(name="learner_leadership_roles", uniqueConstraints=@UniqueConstraint(name="uq_leadership", columnNames={"school_id","learner_id","role_type","academic_year_id"}))
public class LearnerLeadershipRole extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; @Enumerated(EnumType.STRING) private LeadershipRoleType roleType; private Long academicYearId; private LocalDate startDate; private LocalDate endDate; private String notes;
public Long getId(){return id;} public Long getLearnerId(){return learnerId;} public void setLearnerId(Long v){learnerId=v;} public LeadershipRoleType getRoleType(){return roleType;} public void setRoleType(LeadershipRoleType v){roleType=v;} public Long getAcademicYearId(){return academicYearId;} public void setAcademicYearId(Long v){academicYearId=v;} public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate v){startDate=v;} public LocalDate getEndDate(){return endDate;} public void setEndDate(LocalDate v){endDate=v;} public String getNotes(){return notes;} public void setNotes(String v){notes=v;}}
