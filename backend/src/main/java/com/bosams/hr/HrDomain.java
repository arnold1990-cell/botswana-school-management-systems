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

enum StaffType { EDUCATOR, NON_TEACHING }
enum StaffStatus { ACTIVE, ARCHIVED }
enum RegisterClassRole { REGISTER_TEACHER, ASSISTANT }
enum LeaveType { SICK, ANNUAL, MATERNITY, PATERNITY, STUDY, OTHER }
enum LeaveStatus { PENDING, APPROVED, REJECTED, CANCELLED }
enum StaffAttendanceStatus { PRESENT, ABSENT, LATE, OFFICIAL_DUTY }
enum TrainingCategory { INSET, WORKSHOP, COURSE, OTHER }
enum AppraisalType { APPRAISAL, INTERVIEW }

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
abstract class HrSchoolScopedEntity extends AuditableEntity {
    @Column(name = "school_id", nullable = false)
    private Long schoolId;

}
