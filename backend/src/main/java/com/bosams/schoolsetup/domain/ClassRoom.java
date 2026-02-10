package com.bosams.schoolsetup.domain;

import jakarta.persistence.*;

@Entity
@Table(name="classes", uniqueConstraints=@UniqueConstraint(columnNames={"school_id","academic_year_id","code"}))
public class ClassRoom extends SchoolOwnedEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,length=30) private String code;
    @Column(nullable=false,length=100) private String name;
    @ManyToOne(optional=false) @JoinColumn(name="grade_id") private Grade grade;
    @ManyToOne(optional=false) @JoinColumn(name="academic_year_id") private AcademicYear academicYear;
    public Long getId(){return id;} public String getCode(){return code;} public void setCode(String c){code=c;} public String getName(){return name;} public void setName(String n){name=n;}
    public Grade getGrade(){return grade;} public void setGrade(Grade g){grade=g;} public AcademicYear getAcademicYear(){return academicYear;} public void setAcademicYear(AcademicYear a){academicYear=a;}
}
