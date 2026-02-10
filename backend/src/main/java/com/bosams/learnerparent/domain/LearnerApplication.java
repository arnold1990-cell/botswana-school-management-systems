package com.bosams.learnerparent.domain;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name="learner_applications", uniqueConstraints=@UniqueConstraint(name="uq_learner_application_no", columnNames={"school_id","application_no"}))
public class LearnerApplication extends SchoolScopedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name="application_no",nullable=false) private String applicationNo;
    @Column(name="applied_date",nullable=false) private LocalDate appliedDate;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private ApplicationStatus status;
    private Long preferredGradeId; private Long preferredAcademicYearId;
    @Column(nullable=false) private String learnerFirstName; @Column(nullable=false) private String learnerLastName;
    private LocalDate dateOfBirth; private String gender; private String previousSchool;
    @Column(columnDefinition="text") private String notes; @Column(columnDefinition="text") private String decisionNote;
    private Instant decidedAt; private String decidedBy;
    public Long getId(){return id;} public String getApplicationNo(){return applicationNo;} public void setApplicationNo(String v){applicationNo=v;}
    public LocalDate getAppliedDate(){return appliedDate;} public void setAppliedDate(LocalDate v){appliedDate=v;}
    public ApplicationStatus getStatus(){return status;} public void setStatus(ApplicationStatus v){status=v;}
    public Long getPreferredGradeId(){return preferredGradeId;} public void setPreferredGradeId(Long v){preferredGradeId=v;}
    public Long getPreferredAcademicYearId(){return preferredAcademicYearId;} public void setPreferredAcademicYearId(Long v){preferredAcademicYearId=v;}
    public String getLearnerFirstName(){return learnerFirstName;} public void setLearnerFirstName(String v){learnerFirstName=v;}
    public String getLearnerLastName(){return learnerLastName;} public void setLearnerLastName(String v){learnerLastName=v;}
    public LocalDate getDateOfBirth(){return dateOfBirth;} public void setDateOfBirth(LocalDate v){dateOfBirth=v;}
    public String getGender(){return gender;} public void setGender(String v){gender=v;}
    public String getPreviousSchool(){return previousSchool;} public void setPreviousSchool(String v){previousSchool=v;}
    public String getNotes(){return notes;} public void setNotes(String v){notes=v;}
    public String getDecisionNote(){return decisionNote;} public void setDecisionNote(String v){decisionNote=v;}
    public Instant getDecidedAt(){return decidedAt;} public void setDecidedAt(Instant v){decidedAt=v;}
    public String getDecidedBy(){return decidedBy;} public void setDecidedBy(String v){decidedBy=v;}
}
