package com.bosams.hr;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name = "hr_register_class_assignments", uniqueConstraints = @UniqueConstraint(name = "uq_hr_register_class_role", columnNames = {"school_id", "academic_year_id", "class_room_id", "role"}))
public class RegisterClassAssignment extends HrSchoolScopedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; @Column(name = "staff_id", nullable = false) private Long staffId; @Column(name = "class_room_id", nullable = false) private Long classRoomId;
    @Column(name = "academic_year_id", nullable = false) private Long academicYearId; @Enumerated(EnumType.STRING) @Column(nullable = false) private RegisterClassRole role;
    public Long getId() { return id; } public Long getStaffId() { return staffId; } public void setStaffId(Long v) { staffId = v; } public Long getClassRoomId() { return classRoomId; } public void setClassRoomId(Long v) { classRoomId = v; }
    public Long getAcademicYearId() { return academicYearId; } public void setAcademicYearId(Long v) { academicYearId = v; } public RegisterClassRole getRole() { return role; } public void setRole(RegisterClassRole v) { role = v; }
}
