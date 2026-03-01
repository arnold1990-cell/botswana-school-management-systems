package com.bosams.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student")
@Getter
@Setter
@NoArgsConstructor
public class StudentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "admission_no", nullable = false, unique = true)
    private String admissionNo;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Enums.Gender gender;

    @Column(name = "grade_level")
    private Integer gradeLevel;

    @ManyToOne
    @JoinColumn(name = "stream_id")
    private StreamEntity stream;

    @Enumerated(EnumType.STRING)
    private Enums.EntityStatus status = Enums.EntityStatus.ACTIVE;
}
