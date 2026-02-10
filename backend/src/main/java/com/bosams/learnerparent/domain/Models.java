package com.bosams.learnerparent.domain;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;

enum ApplicationStatus { SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, WAITLISTED }
enum LearnerStatus { ACTIVE, ARCHIVED, TRANSFERRED_OUT }
enum TransferType { INTERNAL_CLASS_CHANGE, TRANSFER_OUT, TRANSFER_IN }
enum ParentStatus { ACTIVE, ARCHIVED }
enum RelationshipType { MOTHER, FATHER, GUARDIAN, OTHER }
enum ActivityType { SPORT, ART, CULTURE, CLUB, OTHER }
enum ActivityRole { MEMBER, CAPTAIN, PREFECT, OTHER }
enum LeadershipRoleType { RCL, PREFECT, HEAD_BOY, HEAD_GIRL, OTHER }
enum IncidentCategory { DISCIPLINE, SAFETY, ACADEMIC, OTHER }
enum BarrierType { READING, HEARING, VISION, BEHAVIORAL, OTHER }
enum DisciplineEntryType { MERIT, DEMERIT }
enum DetentionStatus { SCHEDULED, COMPLETED, MISSED, CANCELLED }
enum AttendanceStatus { PRESENT, ABSENT, LATE, EXCUSED }
enum NotificationChannel { IN_APP, EMAIL, SMS }
enum NotificationStatus { PENDING, SENT, FAILED }

@MappedSuperclass
abstract class SchoolScopedEntity extends AuditableEntity {
    @Column(name = "school_id", nullable = false)
    private Long schoolId;
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
}
