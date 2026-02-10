package com.bosams.hr;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity @Table(name = "hr_appraisal_records")
public class AppraisalRecord extends HrSchoolScopedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; @Column(name = "staff_id", nullable = false) private Long staffId; @Column(name = "appraisal_date", nullable = false) private LocalDate appraisalDate;
    @Enumerated(EnumType.STRING) @Column(name = "appraisal_type", nullable = false) private AppraisalType appraisalType; @Column(name = "reviewer_name", nullable = false) private String reviewerName;
    private Integer score; @Column(columnDefinition = "text") private String summary; @Column(columnDefinition = "text") private String recommendations;
    public Long getId() { return id; } public Long getStaffId() { return staffId; } public void setStaffId(Long v) { staffId = v; } public LocalDate getAppraisalDate() { return appraisalDate; } public void setAppraisalDate(LocalDate v) { appraisalDate = v; }
    public AppraisalType getAppraisalType() { return appraisalType; } public void setAppraisalType(AppraisalType v) { appraisalType = v; } public String getReviewerName() { return reviewerName; } public void setReviewerName(String v) { reviewerName = v; }
    public Integer getScore() { return score; } public void setScore(Integer v) { score = v; } public String getSummary() { return summary; } public void setSummary(String v) { summary = v; } public String getRecommendations() { return recommendations; } public void setRecommendations(String v) { recommendations = v; }
}
