package com.bosams.learnerparent.domain;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name="learners", uniqueConstraints=@UniqueConstraint(name="uq_learner_no", columnNames={"school_id","learner_no"}))
public class Learner extends SchoolScopedEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="learner_no",nullable=false) private String learnerNo;
    @Column(nullable=false) private String firstName; @Column(nullable=false) private String lastName;
    private LocalDate dateOfBirth; private String gender; private String nationalId; private String homeLanguage;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private LearnerStatus status;
    private Long currentAcademicYearId; private Long currentGradeId; private Long currentClassRoomId; private Long houseId;
    private LocalDate admissionDate; private Instant archivedAt; private String archiveReason;
    public Long getId(){return id;} public String getLearnerNo(){return learnerNo;} public void setLearnerNo(String v){learnerNo=v;}
    public String getFirstName(){return firstName;} public void setFirstName(String v){firstName=v;} public String getLastName(){return lastName;} public void setLastName(String v){lastName=v;}
    public LocalDate getDateOfBirth(){return dateOfBirth;} public void setDateOfBirth(LocalDate v){dateOfBirth=v;} public String getGender(){return gender;} public void setGender(String v){gender=v;}
    public String getNationalId(){return nationalId;} public void setNationalId(String v){nationalId=v;} public String getHomeLanguage(){return homeLanguage;} public void setHomeLanguage(String v){homeLanguage=v;}
    public LearnerStatus getStatus(){return status;} public void setStatus(LearnerStatus v){status=v;} public Long getCurrentAcademicYearId(){return currentAcademicYearId;} public void setCurrentAcademicYearId(Long v){currentAcademicYearId=v;}
    public Long getCurrentGradeId(){return currentGradeId;} public void setCurrentGradeId(Long v){currentGradeId=v;} public Long getCurrentClassRoomId(){return currentClassRoomId;} public void setCurrentClassRoomId(Long v){currentClassRoomId=v;}
    public Long getHouseId(){return houseId;} public void setHouseId(Long v){houseId=v;} public LocalDate getAdmissionDate(){return admissionDate;} public void setAdmissionDate(LocalDate v){admissionDate=v;}
    public Instant getArchivedAt(){return archivedAt;} public void setArchivedAt(Instant v){archivedAt=v;} public String getArchiveReason(){return archiveReason;} public void setArchiveReason(String v){archiveReason=v;}
}
