package com.bosams.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "mark_entry", uniqueConstraints = @UniqueConstraint(columnNames = {"learner_id", "subject_id", "task_id"}))
@Getter
@Setter
@NoArgsConstructor
public class MarkEntryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "learner_id")
    private StudentEntity learner;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subject_id")
    private SubjectEntity subject;

    @ManyToOne(optional = false)
    @JoinColumn(name = "task_id")
    private AssessmentTaskEntity task;

    @Column(nullable = false)
    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade_letter", nullable = false)
    private Enums.GradeLetter gradeLetter;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt = Instant.now();

    @Column(name = "recorded_by_user_id")
    private UUID recordedByUserId;
}
