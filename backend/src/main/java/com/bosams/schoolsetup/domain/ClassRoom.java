package com.bosams.schoolsetup.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="classes", uniqueConstraints=@UniqueConstraint(columnNames={"school_id","academic_year_id","code"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassRoom extends SchoolOwnedEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,length=30) private String code;
    @Column(nullable=false,length=100) private String name;
    @ManyToOne(optional=false) @JoinColumn(name="grade_id") private Grade grade;
    @ManyToOne(optional=false) @JoinColumn(name="academic_year_id") private AcademicYear academicYear;

}
