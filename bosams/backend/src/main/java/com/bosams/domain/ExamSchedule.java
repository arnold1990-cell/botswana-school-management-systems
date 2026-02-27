package com.bosams.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "exam_schedule")
@Getter @Setter @NoArgsConstructor
public class ExamSchedule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "exam_group_id") private ExamGroup examGroup;
    @ManyToOne(optional = false) @JoinColumn(name = "stream_id") private StreamEntity stream;
    @ManyToOne(optional = false) @JoinColumn(name = "subject_id") private SubjectEntity subject;
    @Column(name = "exam_datetime", nullable = false) private LocalDateTime examDatetime;
    @Column(name = "max_marks", nullable = false, precision = 5, scale = 2) private BigDecimal maxMarks;
    @Column(name = "mark_entry_last_date", nullable = false) private LocalDate markEntryLastDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Enums.EntityStatus status = Enums.EntityStatus.ACTIVE;
}
