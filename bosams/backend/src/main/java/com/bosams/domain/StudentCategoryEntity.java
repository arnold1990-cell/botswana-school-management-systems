package com.bosams.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_category", uniqueConstraints = {
        @UniqueConstraint(name = "uq_student_category_name", columnNames = "name")
})
@Getter
@Setter
@NoArgsConstructor
public class StudentCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Enums.EntityStatus status = Enums.EntityStatus.ACTIVE;
}
