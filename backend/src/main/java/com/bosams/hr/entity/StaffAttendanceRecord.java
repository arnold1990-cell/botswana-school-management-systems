package com.bosams.hr.entity;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name = "hr_staff_attendance_records", uniqueConstraints = @UniqueConstraint(name = "uq_hr_staff_attendance", columnNames = {"school_id", "staff_id", "date"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffAttendanceRecord extends HrSchoolScopedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; @Column(name = "staff_id", nullable = false) private Long staffId; @Column(nullable = false) private LocalDate date;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private StaffAttendanceStatus status; @Column(columnDefinition = "text") private String notes; @Column(name = "captured_by") private String capturedBy; @Column(name = "captured_at", nullable = false) private Instant capturedAt;

}
