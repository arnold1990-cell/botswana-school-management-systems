package com.bosams.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "assessment_task", uniqueConstraints = @UniqueConstraint(columnNames = {"term_id", "type"}))
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Enums.AssessmentType type;

    @Column(name = "max_score", nullable = false)
    private Integer maxScore = 50;
}
