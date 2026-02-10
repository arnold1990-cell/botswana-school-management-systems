package com.bosams.learnerparent.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name="learner_attendance_records", uniqueConstraints=@UniqueConstraint(name="uq_attendance", columnNames={"school_id","learner_id","date","period"}))
public class LearnerAttendanceRecord extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; private Long academicYearId; private Long termId; private LocalDate date; private Integer period; @Enumerated(EnumType.STRING) private AttendanceStatus status; private String notes; private String capturedBy; private Instant capturedAt;
public Long getId(){return id;} public Long getLearnerId(){return learnerId;} public void setLearnerId(Long v){learnerId=v;} public Long getAcademicYearId(){return academicYearId;} public void setAcademicYearId(Long v){academicYearId=v;} public Long getTermId(){return termId;} public void setTermId(Long v){termId=v;} public LocalDate getDate(){return date;} public void setDate(LocalDate v){date=v;} public Integer getPeriod(){return period;} public void setPeriod(Integer v){period=v;} public AttendanceStatus getStatus(){return status;} public void setStatus(AttendanceStatus v){status=v;} public String getNotes(){return notes;} public void setNotes(String v){notes=v;} public String getCapturedBy(){return capturedBy;} public void setCapturedBy(String v){capturedBy=v;} public Instant getCapturedAt(){return capturedAt;} public void setCapturedAt(Instant v){capturedAt=v;}}
