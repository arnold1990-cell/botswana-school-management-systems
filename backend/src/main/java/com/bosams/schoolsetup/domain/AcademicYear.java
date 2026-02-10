package com.bosams.schoolsetup.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "academic_years", uniqueConstraints = @UniqueConstraint(columnNames = {"school_id","name"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicYear extends SchoolOwnedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 100) private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    @Column(nullable = false) private boolean active;

}
