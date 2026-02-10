package com.bosams.hr;

import jakarta.persistence.*;

@Entity @Table(name = "hr_educator_subject_experience", uniqueConstraints = @UniqueConstraint(name = "uq_hr_subject_experience", columnNames = {"school_id", "staff_id", "subject_id"}))
public class EducatorSubjectExperience extends HrSchoolScopedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; @Column(name = "staff_id", nullable = false) private Long staffId; @Column(name = "subject_id", nullable = false) private Long subjectId;
    @Column(name = "years_experience", nullable = false) private Integer yearsExperience; @Column(columnDefinition = "text") private String notes;
    public Long getId() { return id; } public Long getStaffId() { return staffId; } public void setStaffId(Long v) { staffId = v; } public Long getSubjectId() { return subjectId; } public void setSubjectId(Long v) { subjectId = v; }
    public Integer getYearsExperience() { return yearsExperience; } public void setYearsExperience(Integer v) { yearsExperience = v; } public String getNotes() { return notes; } public void setNotes(String v) { notes = v; }
}
