package com.bosams.hr;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name = "hr_training_programs")
public class TrainingProgram extends HrSchoolScopedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; @Column(nullable = false) private String title; private String provider; @Column(name = "start_date", nullable = false) private LocalDate startDate; @Column(name = "end_date", nullable = false) private LocalDate endDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private TrainingCategory category; @Column(columnDefinition = "text") private String notes;
    public Long getId() { return id; } public String getTitle() { return title; } public void setTitle(String v) { title = v; } public String getProvider() { return provider; } public void setProvider(String v) { provider = v; }
    public LocalDate getStartDate() { return startDate; } public void setStartDate(LocalDate v) { startDate = v; } public LocalDate getEndDate() { return endDate; } public void setEndDate(LocalDate v) { endDate = v; }
    public TrainingCategory getCategory() { return category; } public void setCategory(TrainingCategory v) { category = v; } public String getNotes() { return notes; } public void setNotes(String v) { notes = v; }
}
