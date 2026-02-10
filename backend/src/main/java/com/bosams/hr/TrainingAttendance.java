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

@Entity @Table(name = "hr_training_attendance", uniqueConstraints = @UniqueConstraint(name = "uq_hr_training_staff", columnNames = {"school_id", "training_program_id", "staff_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingAttendance extends HrSchoolScopedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; @Column(name = "training_program_id", nullable = false) private Long trainingProgramId; @Column(name = "staff_id", nullable = false) private Long staffId;
    @Column(nullable = false) private boolean attended; @Column(name = "certificate_url") private String certificateUrl;

}
