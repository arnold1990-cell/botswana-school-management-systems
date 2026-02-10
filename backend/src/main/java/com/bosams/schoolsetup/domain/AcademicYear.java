package com.bosams.schoolsetup.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "academic_years", uniqueConstraints = @UniqueConstraint(columnNames = {"school_id","name"}))
public class AcademicYear extends SchoolOwnedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 100) private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    @Column(nullable = false) private boolean active;
    public Long getId(){return id;} public String getName(){return name;} public void setName(String n){name=n;}
    public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate s){startDate=s;}
    public LocalDate getEndDate(){return endDate;} public void setEndDate(LocalDate e){endDate=e;}
    public boolean isActive(){return active;} public void setActive(boolean a){active=a;}
}
