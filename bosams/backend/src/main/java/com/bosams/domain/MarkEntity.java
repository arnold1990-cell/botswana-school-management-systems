package com.bosams.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name = "mark", uniqueConstraints = @UniqueConstraint(columnNames = {"exam_schedule_id", "student_id"}))
@Getter @Setter @NoArgsConstructor
public class MarkEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "exam_schedule_id") private ExamSchedule examSchedule;
    @ManyToOne(optional = false) @JoinColumn(name = "student_id") private StudentEntity student;
    @Column(precision = 5, scale = 2) private BigDecimal marks;
    private boolean absent;
    private String grade;
    @Enumerated(EnumType.STRING) private Enums.MarkResult result;
    private boolean locked;
    @Column(name = "locked_at") private Instant lockedAt;
}
