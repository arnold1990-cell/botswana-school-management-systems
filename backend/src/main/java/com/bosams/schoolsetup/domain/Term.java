package com.bosams.schoolsetup.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "terms", uniqueConstraints = @UniqueConstraint(columnNames = {"academic_year_id","name"}))
public class Term extends SchoolOwnedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "academic_year_id") private AcademicYear academicYear;
    @Column(nullable = false, length = 100) private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    public Long getId(){return id;} public AcademicYear getAcademicYear(){return academicYear;} public void setAcademicYear(AcademicYear a){academicYear=a;}
    public String getName(){return name;} public void setName(String n){name=n;}
    public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate s){startDate=s;}
    public LocalDate getEndDate(){return endDate;} public void setEndDate(LocalDate e){endDate=e;}
}
