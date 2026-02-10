package com.bosams.hr;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

enum StaffType { EDUCATOR, NON_TEACHING }
enum StaffStatus { ACTIVE, ARCHIVED }
enum RegisterClassRole { REGISTER_TEACHER, ASSISTANT }
enum LeaveType { SICK, ANNUAL, MATERNITY, PATERNITY, STUDY, OTHER }
enum LeaveStatus { PENDING, APPROVED, REJECTED, CANCELLED }
enum StaffAttendanceStatus { PRESENT, ABSENT, LATE, OFFICIAL_DUTY }
enum TrainingCategory { INSET, WORKSHOP, COURSE, OTHER }
enum AppraisalType { APPRAISAL, INTERVIEW }

@MappedSuperclass
abstract class HrSchoolScopedEntity extends AuditableEntity {
    @Column(name = "school_id", nullable = false)
    private Long schoolId;
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
}

@Entity
@Table(name = "hr_staff_members", uniqueConstraints = @UniqueConstraint(name = "uq_hr_staff_number", columnNames = {"school_id", "staff_number"}))
class StaffMember extends HrSchoolScopedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "staff_number", nullable = false, length = 40) private String staffNumber;
    @Enumerated(EnumType.STRING) @Column(name = "staff_type", nullable = false) private StaffType staffType;
    @Column(length = 20) private String title;
    @Column(name = "first_name", nullable = false, length = 100) private String firstName;
    @Column(name = "last_name", nullable = false, length = 100) private String lastName;
    @Column(name = "national_id", length = 50) private String nationalId;
    @Column(length = 20) private String gender;
    @Column(name = "date_of_birth") private LocalDate dateOfBirth;
    @Column(name = "employment_start_date") private LocalDate employmentStartDate;
    @Column(name = "employment_end_date") private LocalDate employmentEndDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private StaffStatus status;
    @Column(length = 120) private String email; @Column(length = 40) private String phone;
    @Column(name = "address_line1", length = 120) private String addressLine1; @Column(name = "address_line2", length = 120) private String addressLine2;
    @Column(length = 80) private String city; @Column(length = 80) private String district; @Column(name = "postal_code", length = 20) private String postalCode;
    @Column(name = "archived_at") private Instant archivedAt; @Column(name = "archived_reason", columnDefinition = "text") private String archivedReason;
    public Long getId() { return id; } public String getStaffNumber() { return staffNumber; } public void setStaffNumber(String v) { staffNumber = v; }
    public StaffType getStaffType() { return staffType; } public void setStaffType(StaffType v) { staffType = v; }
    public String getTitle() { return title; } public void setTitle(String v) { title = v; }
    public String getFirstName() { return firstName; } public void setFirstName(String v) { firstName = v; }
    public String getLastName() { return lastName; } public void setLastName(String v) { lastName = v; }
    public String getNationalId() { return nationalId; } public void setNationalId(String v) { nationalId = v; }
    public String getGender() { return gender; } public void setGender(String v) { gender = v; }
    public LocalDate getDateOfBirth() { return dateOfBirth; } public void setDateOfBirth(LocalDate v) { dateOfBirth = v; }
    public LocalDate getEmploymentStartDate() { return employmentStartDate; } public void setEmploymentStartDate(LocalDate v) { employmentStartDate = v; }
    public LocalDate getEmploymentEndDate() { return employmentEndDate; } public void setEmploymentEndDate(LocalDate v) { employmentEndDate = v; }
    public StaffStatus getStatus() { return status; } public void setStatus(StaffStatus v) { status = v; }
    public String getEmail() { return email; } public void setEmail(String v) { email = v; }
    public String getPhone() { return phone; } public void setPhone(String v) { phone = v; }
    public String getAddressLine1() { return addressLine1; } public void setAddressLine1(String v) { addressLine1 = v; }
    public String getAddressLine2() { return addressLine2; } public void setAddressLine2(String v) { addressLine2 = v; }
    public String getCity() { return city; } public void setCity(String v) { city = v; }
    public String getDistrict() { return district; } public void setDistrict(String v) { district = v; }
    public String getPostalCode() { return postalCode; } public void setPostalCode(String v) { postalCode = v; }
    public Instant getArchivedAt() { return archivedAt; } public void setArchivedAt(Instant v) { archivedAt = v; }
    public String getArchivedReason() { return archivedReason; } public void setArchivedReason(String v) { archivedReason = v; }
}

@Entity @Table(name = "hr_educator_subject_experience", uniqueConstraints = @UniqueConstraint(name = "uq_hr_subject_experience", columnNames = {"school_id", "staff_id", "subject_id"}))
class EducatorSubjectExperience extends HrSchoolScopedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; @Column(name = "staff_id", nullable = false) private Long staffId; @Column(name = "subject_id", nullable = false) private Long subjectId;
    @Column(name = "years_experience", nullable = false) private Integer yearsExperience; @Column(columnDefinition = "text") private String notes;
    public Long getId() { return id; } public Long getStaffId() { return staffId; } public void setStaffId(Long v) { staffId = v; } public Long getSubjectId() { return subjectId; } public void setSubjectId(Long v) { subjectId = v; }
    public Integer getYearsExperience() { return yearsExperience; } public void setYearsExperience(Integer v) { yearsExperience = v; } public String getNotes() { return notes; } public void setNotes(String v) { notes = v; }
}

@Entity @Table(name = "hr_register_class_assignments", uniqueConstraints = @UniqueConstraint(name = "uq_hr_register_class_role", columnNames = {"school_id", "academic_year_id", "class_room_id", "role"}))
class RegisterClassAssignment extends HrSchoolScopedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; @Column(name = "staff_id", nullable = false) private Long staffId; @Column(name = "class_room_id", nullable = false) private Long classRoomId;
    @Column(name = "academic_year_id", nullable = false) private Long academicYearId; @Enumerated(EnumType.STRING) @Column(nullable = false) private RegisterClassRole role;
    public Long getId() { return id; } public Long getStaffId() { return staffId; } public void setStaffId(Long v) { staffId = v; } public Long getClassRoomId() { return classRoomId; } public void setClassRoomId(Long v) { classRoomId = v; }
    public Long getAcademicYearId() { return academicYearId; } public void setAcademicYearId(Long v) { academicYearId = v; } public RegisterClassRole getRole() { return role; } public void setRole(RegisterClassRole v) { role = v; }
}

@Entity @Table(name = "hr_staff_leave_requests")
class StaffLeaveRequest extends HrSchoolScopedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; @Column(name = "staff_id", nullable = false) private Long staffId; @Enumerated(EnumType.STRING) @Column(name = "leave_type", nullable = false) private LeaveType leaveType;
    @Column(name = "start_date", nullable = false) private LocalDate startDate; @Column(name = "end_date", nullable = false) private LocalDate endDate; @Column(columnDefinition = "text") private String reason;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private LeaveStatus status; @Column(name = "decided_by") private String decidedBy; @Column(name = "decided_at") private Instant decidedAt; @Column(name = "decision_note", columnDefinition = "text") private String decisionNote;
    public Long getId() { return id; } public Long getStaffId() { return staffId; } public void setStaffId(Long v) { staffId = v; } public LeaveType getLeaveType() { return leaveType; } public void setLeaveType(LeaveType v) { leaveType = v; }
    public LocalDate getStartDate() { return startDate; } public void setStartDate(LocalDate v) { startDate = v; } public LocalDate getEndDate() { return endDate; } public void setEndDate(LocalDate v) { endDate = v; }
    public String getReason() { return reason; } public void setReason(String v) { reason = v; } public LeaveStatus getStatus() { return status; } public void setStatus(LeaveStatus v) { status = v; }
    public String getDecidedBy() { return decidedBy; } public void setDecidedBy(String v) { decidedBy = v; } public Instant getDecidedAt() { return decidedAt; } public void setDecidedAt(Instant v) { decidedAt = v; } public String getDecisionNote() { return decisionNote; } public void setDecisionNote(String v) { decisionNote = v; }
}

@Entity @Table(name = "hr_staff_attendance_records", uniqueConstraints = @UniqueConstraint(name = "uq_hr_staff_attendance", columnNames = {"school_id", "staff_id", "date"}))
class StaffAttendanceRecord extends HrSchoolScopedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; @Column(name = "staff_id", nullable = false) private Long staffId; @Column(nullable = false) private LocalDate date;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private StaffAttendanceStatus status; @Column(columnDefinition = "text") private String notes; @Column(name = "captured_by") private String capturedBy; @Column(name = "captured_at", nullable = false) private Instant capturedAt;
    public Long getId() { return id; } public Long getStaffId() { return staffId; } public void setStaffId(Long v) { staffId = v; } public LocalDate getDate() { return date; } public void setDate(LocalDate v) { date = v; }
    public StaffAttendanceStatus getStatus() { return status; } public void setStatus(StaffAttendanceStatus v) { status = v; } public String getNotes() { return notes; } public void setNotes(String v) { notes = v; }
    public String getCapturedBy() { return capturedBy; } public void setCapturedBy(String v) { capturedBy = v; } public Instant getCapturedAt() { return capturedAt; } public void setCapturedAt(Instant v) { capturedAt = v; }
}

@Entity @Table(name = "hr_training_programs")
class TrainingProgram extends HrSchoolScopedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; @Column(nullable = false) private String title; private String provider; @Column(name = "start_date", nullable = false) private LocalDate startDate; @Column(name = "end_date", nullable = false) private LocalDate endDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private TrainingCategory category; @Column(columnDefinition = "text") private String notes;
    public Long getId() { return id; } public String getTitle() { return title; } public void setTitle(String v) { title = v; } public String getProvider() { return provider; } public void setProvider(String v) { provider = v; }
    public LocalDate getStartDate() { return startDate; } public void setStartDate(LocalDate v) { startDate = v; } public LocalDate getEndDate() { return endDate; } public void setEndDate(LocalDate v) { endDate = v; }
    public TrainingCategory getCategory() { return category; } public void setCategory(TrainingCategory v) { category = v; } public String getNotes() { return notes; } public void setNotes(String v) { notes = v; }
}

@Entity @Table(name = "hr_training_attendance", uniqueConstraints = @UniqueConstraint(name = "uq_hr_training_staff", columnNames = {"school_id", "training_program_id", "staff_id"}))
class TrainingAttendance extends HrSchoolScopedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; @Column(name = "training_program_id", nullable = false) private Long trainingProgramId; @Column(name = "staff_id", nullable = false) private Long staffId;
    @Column(nullable = false) private boolean attended; @Column(name = "certificate_url") private String certificateUrl;
    public Long getId() { return id; } public Long getTrainingProgramId() { return trainingProgramId; } public void setTrainingProgramId(Long v) { trainingProgramId = v; } public Long getStaffId() { return staffId; } public void setStaffId(Long v) { staffId = v; }
    public boolean isAttended() { return attended; } public void setAttended(boolean v) { attended = v; } public String getCertificateUrl() { return certificateUrl; } public void setCertificateUrl(String v) { certificateUrl = v; }
}

@Entity @Table(name = "hr_appraisal_records")
class AppraisalRecord extends HrSchoolScopedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; @Column(name = "staff_id", nullable = false) private Long staffId; @Column(name = "appraisal_date", nullable = false) private LocalDate appraisalDate;
    @Enumerated(EnumType.STRING) @Column(name = "appraisal_type", nullable = false) private AppraisalType appraisalType; @Column(name = "reviewer_name", nullable = false) private String reviewerName;
    private Integer score; @Column(columnDefinition = "text") private String summary; @Column(columnDefinition = "text") private String recommendations;
    public Long getId() { return id; } public Long getStaffId() { return staffId; } public void setStaffId(Long v) { staffId = v; } public LocalDate getAppraisalDate() { return appraisalDate; } public void setAppraisalDate(LocalDate v) { appraisalDate = v; }
    public AppraisalType getAppraisalType() { return appraisalType; } public void setAppraisalType(AppraisalType v) { appraisalType = v; } public String getReviewerName() { return reviewerName; } public void setReviewerName(String v) { reviewerName = v; }
    public Integer getScore() { return score; } public void setScore(Integer v) { score = v; } public String getSummary() { return summary; } public void setSummary(String v) { summary = v; } public String getRecommendations() { return recommendations; } public void setRecommendations(String v) { recommendations = v; }
}
