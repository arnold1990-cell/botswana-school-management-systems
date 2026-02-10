package com.bosams.hr;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name = "hr_training_attendance", uniqueConstraints = @UniqueConstraint(name = "uq_hr_training_staff", columnNames = {"school_id", "training_program_id", "staff_id"}))
public class TrainingAttendance extends HrSchoolScopedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; @Column(name = "training_program_id", nullable = false) private Long trainingProgramId; @Column(name = "staff_id", nullable = false) private Long staffId;
    @Column(nullable = false) private boolean attended; @Column(name = "certificate_url") private String certificateUrl;
    public Long getId() { return id; } public Long getTrainingProgramId() { return trainingProgramId; } public void setTrainingProgramId(Long v) { trainingProgramId = v; } public Long getStaffId() { return staffId; } public void setStaffId(Long v) { staffId = v; }
    public boolean isAttended() { return attended; } public void setAttended(boolean v) { attended = v; } public String getCertificateUrl() { return certificateUrl; } public void setCertificateUrl(String v) { certificateUrl = v; }
}
