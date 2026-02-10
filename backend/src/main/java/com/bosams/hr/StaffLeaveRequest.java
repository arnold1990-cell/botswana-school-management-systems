package com.bosams.hr;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name = "hr_staff_leave_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffLeaveRequest extends HrSchoolScopedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; @Column(name = "staff_id", nullable = false) private Long staffId; @Enumerated(EnumType.STRING) @Column(name = "leave_type", nullable = false) private LeaveType leaveType;
    @Column(name = "start_date", nullable = false) private LocalDate startDate; @Column(name = "end_date", nullable = false) private LocalDate endDate; @Column(columnDefinition = "text") private String reason;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private LeaveStatus status; @Column(name = "decided_by") private String decidedBy; @Column(name = "decided_at") private Instant decidedAt; @Column(name = "decision_note", columnDefinition = "text") private String decisionNote;

}
