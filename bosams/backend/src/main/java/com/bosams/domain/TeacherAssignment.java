package com.bosams.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teacher_assignment", uniqueConstraints = @UniqueConstraint(columnNames = {"teacher_user_id", "academic_year_id", "stream_id", "subject_id"}))
@Getter @Setter @NoArgsConstructor
public class TeacherAssignment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "teacher_user_id") private UserEntity teacher;
    @ManyToOne(optional = false) @JoinColumn(name = "academic_year_id") private AcademicYear academicYear;
    @ManyToOne(optional = false) @JoinColumn(name = "stream_id") private StreamEntity stream;
    @ManyToOne(optional = false) @JoinColumn(name = "subject_id") private SubjectEntity subject;
    @Column(nullable = false) private boolean active = true;
}
