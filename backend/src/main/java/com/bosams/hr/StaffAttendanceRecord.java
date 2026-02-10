package com.bosams.hr;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name = "hr_staff_attendance_records", uniqueConstraints = @UniqueConstraint(name = "uq_hr_staff_attendance", columnNames = {"school_id", "staff_id", "date"}))
public class StaffAttendanceRecord extends HrSchoolScopedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; @Column(name = "staff_id", nullable = false) private Long staffId; @Column(nullable = false) private LocalDate date;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private StaffAttendanceStatus status; @Column(columnDefinition = "text") private String notes; @Column(name = "captured_by") private String capturedBy; @Column(name = "captured_at", nullable = false) private Instant capturedAt;
    public Long getId() { return id; } public Long getStaffId() { return staffId; } public void setStaffId(Long v) { staffId = v; } public LocalDate getDate() { return date; } public void setDate(LocalDate v) { date = v; }
    public StaffAttendanceStatus getStatus() { return status; } public void setStatus(StaffAttendanceStatus v) { status = v; } public String getNotes() { return notes; } public void setNotes(String v) { notes = v; }
    public String getCapturedBy() { return capturedBy; } public void setCapturedBy(String v) { capturedBy = v; } public Instant getCapturedAt() { return capturedAt; } public void setCapturedAt(Instant v) { capturedAt = v; }
}
