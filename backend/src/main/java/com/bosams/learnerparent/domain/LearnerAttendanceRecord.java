package com.bosams.learnerparent.domain;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name="learner_attendance_records", uniqueConstraints=@UniqueConstraint(name="uq_attendance", columnNames={"school_id","learner_id","date","period"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearnerAttendanceRecord extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; private Long academicYearId; private Long termId; private LocalDate date; private Integer period; @Enumerated(EnumType.STRING) private AttendanceStatus status; private String notes; private String capturedBy; private Instant capturedAt;

}
