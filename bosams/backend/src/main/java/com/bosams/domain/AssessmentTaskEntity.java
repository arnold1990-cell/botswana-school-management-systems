package com.bosams.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "assessment_task")
@Getter
@Setter
@NoArgsConstructor
public class AssessmentTaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "term_id")
    private Term term;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private SubjectEntity subject;

    @Column(name = "grade_level")
    private Integer gradeLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Enums.AssessmentType type;

    @Column(nullable = false)
    private String title;

    @Column(name = "description_text")
    private String description;

    @Column(name = "due_date")
    private java.time.LocalDate dueDate;

    @Column(name = "created_by_user_id")
    private java.util.UUID createdByUserId;

    @Column(name = "max_score", nullable = false)
    private Integer maxScore = 50;
}
