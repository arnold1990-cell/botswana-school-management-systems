package com.bosams.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "teacher_assignment", uniqueConstraints = @UniqueConstraint(columnNames = {"teacher_user_id", "academic_year_id", "grade_level", "subject_id", "stream_id"}))
@Getter
@Setter
@NoArgsConstructor
public class TeacherAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "teacher_user_id")
    private UserEntity teacher;

    @ManyToOne(optional = false)
    @JoinColumn(name = "academic_year_id")
    private AcademicYear academicYear;

    @Column(name = "grade_level", nullable = false)
    private Integer gradeLevel;

    @ManyToOne
    @JoinColumn(name = "stream_id")
    private StreamEntity stream;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subject_id")
    private SubjectEntity subject;

    @Column(nullable = false)
    private boolean active = true;
}
