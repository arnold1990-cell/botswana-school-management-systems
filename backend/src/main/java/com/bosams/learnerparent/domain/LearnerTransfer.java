package com.bosams.learnerparent.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name="learner_transfers")
public class LearnerTransfer extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; @Enumerated(EnumType.STRING) private TransferType transferType;
private Long fromAcademicYearId; private Long fromClassRoomId; private Long fromGradeId; private Long toAcademicYearId; private Long toClassRoomId; private Long toGradeId; private LocalDate effectiveDate; private String reason; private String capturedBy; private Instant capturedAt;
public Long getId(){return id;} public Long getLearnerId(){return learnerId;} public void setLearnerId(Long v){learnerId=v;} public TransferType getTransferType(){return transferType;} public void setTransferType(TransferType v){transferType=v;}
public Long getFromAcademicYearId(){return fromAcademicYearId;} public void setFromAcademicYearId(Long v){fromAcademicYearId=v;} public Long getFromClassRoomId(){return fromClassRoomId;} public void setFromClassRoomId(Long v){fromClassRoomId=v;} public Long getFromGradeId(){return fromGradeId;} public void setFromGradeId(Long v){fromGradeId=v;}
public Long getToAcademicYearId(){return toAcademicYearId;} public void setToAcademicYearId(Long v){toAcademicYearId=v;} public Long getToClassRoomId(){return toClassRoomId;} public void setToClassRoomId(Long v){toClassRoomId=v;} public Long getToGradeId(){return toGradeId;} public void setToGradeId(Long v){toGradeId=v;}
public LocalDate getEffectiveDate(){return effectiveDate;} public void setEffectiveDate(LocalDate v){effectiveDate=v;} public String getReason(){return reason;} public void setReason(String v){reason=v;} public String getCapturedBy(){return capturedBy;} public void setCapturedBy(String v){capturedBy=v;} public Instant getCapturedAt(){return capturedAt;} public void setCapturedAt(Instant v){capturedAt=v;}
}
