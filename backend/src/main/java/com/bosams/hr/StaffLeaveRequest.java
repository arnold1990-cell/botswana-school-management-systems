package com.bosams.hr;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name = "hr_staff_leave_requests")
public class StaffLeaveRequest extends HrSchoolScopedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; @Column(name = "staff_id", nullable = false) private Long staffId; @Enumerated(EnumType.STRING) @Column(name = "leave_type", nullable = false) private LeaveType leaveType;
    @Column(name = "start_date", nullable = false) private LocalDate startDate; @Column(name = "end_date", nullable = false) private LocalDate endDate; @Column(columnDefinition = "text") private String reason;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private LeaveStatus status; @Column(name = "decided_by") private String decidedBy; @Column(name = "decided_at") private Instant decidedAt; @Column(name = "decision_note", columnDefinition = "text") private String decisionNote;
    public Long getId() { return id; } public Long getStaffId() { return staffId; } public void setStaffId(Long v) { staffId = v; } public LeaveType getLeaveType() { return leaveType; } public void setLeaveType(LeaveType v) { leaveType = v; }
    public LocalDate getStartDate() { return startDate; } public void setStartDate(LocalDate v) { startDate = v; } public LocalDate getEndDate() { return endDate; } public void setEndDate(LocalDate v) { endDate = v; }
    public String getReason() { return reason; } public void setReason(String v) { reason = v; } public LeaveStatus getStatus() { return status; } public void setStatus(LeaveStatus v) { status = v; }
    public String getDecidedBy() { return decidedBy; } public void setDecidedBy(String v) { decidedBy = v; } public Instant getDecidedAt() { return decidedAt; } public void setDecidedAt(Instant v) { decidedAt = v; } public String getDecisionNote() { return decisionNote; } public void setDecisionNote(String v) { decisionNote = v; }
}
